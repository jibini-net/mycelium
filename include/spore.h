#pragma once

#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>

#include "packet.h"
#include "uuid.h"
#include "patch.h"
#include "router.h"

typedef void (*pckt_handle_fun)(pckt_t *);

// A single Mycelium routing node
struct spore_t
{
    // Network node's packet router
    router_t router;
    // Node's attachment to its router
    patch_t patch;
    endpt_t to_router;
    // State of node; alive or not
    bool alive;
    // Remember this node's address
    uuid_t attach;
};
typedef struct spore_t spore_t;

/**
 * @param spore Spore which will be initialized and started.
 */
void create_spore(spore_t *spore);

/**
 * @param spore Spore whose routing and resources will be destroyed.
 */
void free_spore(spore_t *spore);