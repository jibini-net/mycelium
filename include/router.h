#pragma once

#include <stdbool.h>
#include <threads.h>

#include "packet.h"
#include "uuid.h"
#include "patch.h"

/**
 * An active process which accepts packets and routes them to attached
 * addressed endpoints.
 */
struct router_t
{
    // Each router has a unique identifier
    uuid_t uuid;
    // Remember where to route packets for certain addresses
    table_t route_table;
    // Keep track of attachments and their UUIDs
    table_t attachments;

    // Whether the router is alive and its thread handle
    bool alive;
    thrd_t thread;
};
typedef struct router_t *router_t;

/**
 * @return Created and started router handle.
 */
router_t create_router();

/**
 * @param router Router to stop and free its memory.
 */
void free_router(router_t router);

/**
 * Attaches an endpoint to the provided router. Data pulled from the provided
 * endpoint can be routed by the router thread.
 * 
 * @param router Router to which the endpoint will be attached.
 * @param endpoint Endpoint to attach to the router.
 * 
 * @return The UUID of the attachment on the router; it is random and unique.
 *      Elements send to this address over the Mycelium network will be routed
 *      to and received by the entity which attaches the parameter endpoint.
 */
uuid_t router_attach(router_t router, endpt_t endpoint);