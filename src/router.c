#include "router.h"

#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <threads.h>

#include "packet.h"

#define REFRESH_WAIT_NANO 4000000
#define ROUTER_BUFFER_SIZE 64

thread_local router_t thread_owner;

// Evaluates routing tables and sends the packet in the correct direction
void _route_packet(router_t router, pckt_t packet)
{
    // Route to target if attached to same router
    uuid_t endpoint_ptr = *(uuid_t *)pckt_get(packet, "target", NULL);
    endpt_t endpoint = *(endpt_t *)((long)table_get(router->attachments, endpoint_ptr));
    // Push through selected endpoint (to fellow router attachment)
    endpt_push(endpoint, packet);
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
        pckt_t packet = (pckt_t)endpt_pull(endpoint);
        // Leave routing breadcrumb
        table_put(packet->manifest, thread_owner->uuid, id);
        _route_packet(thread_owner, packet);
    }
}

// Maintains a router's connectivity at a set refresh interval
void _router_thrd(router_t router)
{
    // This thread is dedicated to one router
    thread_owner = router;
    // Iterate until the router is freed or dies
    while (router->alive)
    {
        // Iterate through attached endpoints
        table_it(router->attachments, (table_it_fun)_router_update_attach);

        struct timespec t;
        t.tv_sec = 0;
        t.tv_nsec = REFRESH_WAIT_NANO;
        // Sleep until next refresh cycle (~250 Hz)
        nanosleep(&t, &t);
    }
}

router_t create_router()
{
    router_t result = (router_t)malloc(sizeof(struct router_t));
    result->uuid = create_uuid();
    result->alive = true;
    result->route_table = create_table();
    result->attachments = create_table();

    // Spawn a thread to route this router's traffic
    thrd_create(&result->thread, (thrd_start_t)_router_thrd, (void *)result);
    thrd_detach(result->thread);

    return result;
}

void free_router(router_t router)
{
    // Wait for the routing thread to finish
    router->alive = false;
    thrd_join(router->thread, NULL);

    free_table(router->route_table);
    free_table(router->attachments);
    
    free(router);
}

uuid_t router_attach(router_t router, endpt_t endpoint)
{
    // Attach at a random UUID
    uuid_t uuid = create_uuid();
    // Put in packet manifest
    endpt_t *copy = (endpt_t *)malloc(sizeof(endpt_t));
    memcpy(copy, &endpoint, sizeof(endpt_t));
    table_put(router->attachments, uuid, (uuid_t)((long)copy));

    return uuid;
}