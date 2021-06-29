#include "spore.h"

#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdint.h>
#include <string.h>

#include "router.h"

// Network node's packet router
router_t _n_router;
// Node's attachment to its router
patch_t _n_patch;
endpt_t _n_to_router;
// State of node; alive or not
bool alive = true;
// Remember this node's address
uuid_t _n_attach;

int main(int args_c, char **args)
{
    printf("╒════════════════════════════════════════════════╕\n");
    printf("│|             MYCELIUM: Spore Node             |│\n");
    printf("╘════════════════════════════════════════════════╛\n\n");
    printf("Initializing node routing components . . .\n");
    // Create components of node
    create_router(&_n_router);
    create_patch(&_n_patch, ROUTER_BUFFER_SIZE);
    printf("Attaching network primitives . . .\n");
    // Create connections
    endpt_t _n_up = patch_endpt(&_n_patch, UPSTREAM);
    _n_to_router = patch_endpt(&_n_patch, DOWNSTREAM);
    // Attach node to its router
    _n_attach = router_attach(&_n_router, _n_up);
    printf("Node is now online for locally scoped communication.\n");

    printf("\n ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n");
    printf(" ┃  Node '%s'  ┃\n", uuid_to_string(_n_attach, true));
    printf(" ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫\n");
    printf("   Router buffer size: %d\n", ROUTER_BUFFER_SIZE);
    printf("   Router refresh rate: %d Hz\n", 1000000000 / REFRESH_WAIT_NANO);
    printf(" ┕━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛\n\n");

    printf("Mycelium Spore node is active and routing packets.\n\n");
    while (alive)
    {
        pckt_t *packet = (pckt_t *)endpt_pull(_n_to_router);
        printf("(spore.c:29) Received a packet\n");
    }

    printf("Node has shut down; freeing components . . .\n");
    free_router(&_n_router);
    free_patch(&_n_patch);

    printf("\nGoodbye!\n");
}