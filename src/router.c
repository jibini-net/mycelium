#include "router.h"

#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <threads.h>

#include "packet.h"

#define REFRESH_WAIT_NANO 4000000
#define ROUTER_BUFFER_SIZE 64

thread_local router_t *thread_owner;

// Evaluates routing tables and sends the packet in the correct direction
void _route_packet(router_t *router, pckt_t *packet)
{
    // Route to target if attached to same router
    uuid_t endpoint_id = *(uuid_t *)pckt_get(packet, "target", NULL);
    endpt_t endpoint = *(endpt_t *)((long)table_get(&router->attachments, endpoint_id));
    // Push through selected endpoint (to fellow router attachment)
    endpt_push(endpoint, (data_t)packet);
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
        // Leave routing breadcrumb
        table_put(&packet->manifest, thread_owner->uuid, id);
        _route_packet(thread_owner, packet);
    }
}

// Maintains a router's connectivity at a set refresh interval
void _router_thrd(router_t *router)
{
    // This thread is dedicated to one router
    thread_owner = router;
    // Iterate until the router is freed or dies
    while (router->alive)
    {
        // Iterate through attached endpoints
        table_it(&router->attachments, (table_it_fun)_router_update_attach);

        struct timespec t;
        t.tv_sec = 0;
        t.tv_nsec = REFRESH_WAIT_NANO;
        // Sleep until next refresh cycle (~250 Hz)
        nanosleep(&t, &t);
    }
}

void create_router(router_t *router)
{
    router->uuid = random_uuid();
    router->alive = true;
    create_table(&router->route_table);
    create_table(&router->attachments);

    // Spawn a thread to route this router's traffic
    thrd_create(&router->thread, (thrd_start_t)_router_thrd, (void *)router);
    thrd_detach(router->thread);
}

void free_router(router_t *router)
{
    // Wait for the routing thread to finish
    router->alive = false;
    thrd_join(router->thread, NULL);

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

    return uuid;
}