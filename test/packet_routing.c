#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>

#include "packet.h"
#include "uuid.h"
#include "patch.h"
#include "router.h"

int test_router_send();

#define TEST_BOUNDARY(append) printf("=================================%s\n", append)
#define TEST(name, task, fail) TEST_BOUNDARY("");\
    printf("Running test: '%s'\n---------------------------------\n", name);\
    *fail += task();\
    TEST_BOUNDARY("\n");

int main(int args_c, char **args)
{
    int fail = 0;

    TEST("Router packet send (sanity check)", test_router_send, &fail);

    return fail;
}

int test_router_send()
{
    // Allocate space for router and patches
    router_t router;
    patch_t a, b;
    // Initialize router and patches
    create_router(&router);
    create_patch(&a, 32);
    create_patch(&b, 32);
    // Create duplex endpoints for both patches
    endpt_t up_a = patch_endpt(&a, UPSTREAM);
    endpt_t down_a = patch_endpt(&a, DOWNSTREAM);
    endpt_t up_b = patch_endpt(&b, UPSTREAM);
    endpt_t down_b = patch_endpt(&b, DOWNSTREAM);
    // Attach upstreams of patches to router
    uuid_t id = router_attach(&router, up_a);
    router_attach(&router, up_b);

    // Allocate and create new packet
    pckt_t packet;
    create_pckt(&packet);
    // Create packet data (target to patch A)
    printf("Sending a packet to '%s'\n", uuid_to_string(id, true));
    pckt_put(&packet, "target", (void *)&id);
    pckt_put(&packet, "message", strdup("Hello, world!"));
    // Send data to patch A via router
    endpt_push(down_b, (data_t)&packet);

    // Pull the packet from A downstream from router
    pckt_t *pull = (pckt_t *)endpt_pull(down_a);
    // Get message sent from B
    char *message = pckt_get(pull, "message", NULL);
    printf("Pulled something down: '%s'\n", message);

    // Free packet, patches, and router
    free_pckt(&packet);
    free_patch(&a);
    free_patch(&b);
    free_router(&router);

    return 0;
}