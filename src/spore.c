#include "spore.h"

spore_t _n_spore;

void _handle_packet(pckt_t *packet, endpt_t from)
{
    printf("(spore.c:7) Received a packet; returning to sender\n");
    endpt_push(from, packet);
}

int main(int args_c, char **args)
{
    printf("╒════════════════════════════════════════════════╕\n");
    printf("│|             MYCELIUM: Spore Node             |│\n");
    printf("╘════════════════════════════════════════════════╛\n\n");
    create_spore(&_n_spore);

    printf("\n ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n");
    printf(" ┃  Node '%s'  ┃\n", uuid_to_string(_n_spore.attach, true));
    printf(" ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫\n");
    printf("   Router '%s'\n", uuid_to_string(_n_spore.router.uuid, true));
    printf("   Buffer size: %d\n", ROUTER_BUFFER_SIZE);
    printf("   Refresh rate: %d Hz\n", 1000000000 / REFRESH_WAIT_NANO);
    printf(" ┕━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛\n\n");

    printf("Mycelium Spore node is active and routing packets.\n\n");

    while (_n_spore.alive)
    {
        pckt_t *packet = (pckt_t *)endpt_pull(_n_spore.to_router);
        _handle_packet(packet, _n_spore.to_router);
    }

    printf("Node has shut down; freeing components . . .\n");
    free_spore(&_n_spore);

    printf("\nGoodbye!\n\n");
}

void create_spore(spore_t *spore)
{
    printf("Initializing node routing components . . .\n");
    // Create components of node
    create_router(&spore->router);
    create_patch(&spore->patch, ROUTER_BUFFER_SIZE);

    printf("Attaching network primitives . . .\n");
    // Create connections
    endpt_t node_up = patch_endpt(&spore->patch, UPSTREAM);
    spore->to_router = patch_endpt(&spore->patch, DOWNSTREAM);
    // Attach node to its router
    spore->attach = router_attach(&spore->router, node_up);

    printf("Node is now online for locally scoped communication.\n");
}

void free_spore(spore_t *spore)
{
    free_router(&spore->router);
    free_patch(&spore->patch);
}