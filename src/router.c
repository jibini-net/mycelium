#include "router.h"

#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>

#include <unistd.h>
#include <threads.h>

#include "packet.h"

thread_local router_t *_r_router = NULL;
thread_local pckt_t *_r_packet = NULL;
thread_local uuid_t _r_recipient = 0;

bool _send_packet(uuid_t to)
{
    if (to == 0) return false;

    uuid_t e_to = table_get(&_r_router->attachments, to);
    // Attachment is not found; drop the packet
    if (e_to == 0) return false;
    
    // Leave routing breadcrumb
    table_put(&_r_packet->route, _r_router->uuid, _r_recipient);
    _r_packet->route_c++;
    // Push through endpoint (to peer router attachment)
    endpt_t endpoint = *(endpt_t *)((long)e_to);
    endpt_push(endpoint, _r_packet);

    return true;
}

// Check for a routing crumb for return routing
bool _route_packet1()
{
    // Check manifest for routing 
    uuid_t to = table_get(&_r_packet->route, _r_router->uuid);
    // Try to send return routing
    return _send_packet(to);
}

// Check if the target is attached (is peer)
bool _route_packet2()
{
    // Route to target if attached to same router
    uuid_t id = *(uuid_t *)pckt_get(_r_packet, "target", NULL);
    // Attempt to send packet to peer attached to router
    return _send_packet(id);
}

// Check for any routing table entry for the target
bool _route_packet3()
{
    // Get the target of the packet
    uuid_t id = *(uuid_t *)pckt_get(_r_packet, "target", NULL);
    // Check manifest for routing for the target
    uuid_t to = table_get(&_r_router->route_table, id);
    // Attempt to send indirectly to target
    return _send_packet(to);
}

// Evaluates routing tables and sends the packet in the correct direction
void _route_packet(router_t *router, pckt_t *packet, uuid_t recipient)
{
    /*
     * ====================================================
     * | | | | | EVALUATE IN THE FOLLOWING ORDER: | | | | |
     * ====================================================
     *  1. Check for a routing crumb for return routing
     *  2. Check if the target is attached (is peer)
     *  3. Check for any routing table entry for the target
     *  4. Send to upstream
     *  5. Drop
     * ====================================================
     */

    _r_router = router;
    _r_packet = packet;
    _r_recipient = recipient;

    // Attempt to evaluate routing
    if (_route_packet1()) return;
    if (_route_packet2()) return;
    if (_route_packet3()) return;

    // Last choice; send to upstream
    _send_packet(_r_router->upstream);
}

// Iterator function for router endpoint refresh cycle
void _router_update_attach(uuid_t id, uuid_t endpt_ptr)
{
    // Grab each of the attachment's endpoints
    endpt_t endpoint = *(endpt_t *)_uuid_to_ptr_a(endpt_ptr);
    // Check if any have available packets
    if (endpt_peek(endpoint))
    {
        // Pull pointer to in-memory packet
        pckt_t *packet = (pckt_t *)endpt_pull(endpoint);
        // Attempt to route the packet
        _route_packet(_r_router, packet, id);
    }
}

// Maintains a router's connectivity at a set refresh interval
void _router_thrd(router_t *router)
{
    // This thread is dedicated to one router
    _r_router = router;
    // Iterate until the router is freed or dies
    while (router->alive)
    {
        // Iterate through attached endpoints
        table_it(&router->attachments, (table_it_fun)_router_update_attach);

        struct timespec t;
        t.tv_sec = 0;
        t.tv_nsec = REFRESH_WAIT_NANO;
        // Sleep until next refresh cycle (~2 kHz)
        nanosleep(&t, &t);
    }

    thrd_exit(thrd_success);
}

void create_router(router_t *router)
{
    router->uuid = random_uuid();
    router->alive = true;

    printf("Creating state tables for router '%s' . . .\n", uuid_to_string(router->uuid, true));
    create_table(&router->route_table);
    create_table(&router->attachments);
    printf("Starting routing cycle thread . . .\n");
    // Spawn a thread to route this router's traffic
    thrd_create(&router->thread, (thrd_start_t)_router_thrd, (void *)router);
    thrd_detach(router->thread);
    
    printf("Default router has no assigned upstream parent.\n");
    router->upstream = 0;
}

void _free_router_assoc_table(uuid_t uuid, uuid_t endpt_ptr)
{
    free((void *)((long)endpt_ptr));
}

void free_router(router_t *router)
{
    // Wait for the routing thread to finish
    router->alive = false;
    thrd_join(router->thread, NULL);
    // Free copies of attachment endpoints
    table_it(&router->attachments, (table_it_fun)_free_router_assoc_table);

    free_table(&router->route_table);
    free_table(&router->attachments);
}

uuid_t router_attach(router_t *router, endpt_t endpoint)
{
    // Attach at a random UUID
    uuid_t uuid = random_uuid();
    // Put in packet manifest
    endpt_t *copy = (endpt_t *)malloc(sizeof(endpt_t));
    memcpy(copy, &endpoint, sizeof(endpt_t));
    table_put(&router->attachments, uuid, (uuid_t)((long)copy));

    printf("Router '%s' has attached at '%s'.\n", uuid_to_string(router->uuid, true),
        uuid_to_string(uuid, true));

    return uuid;
}