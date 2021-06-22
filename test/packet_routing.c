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
    router_t router = create_router();
    patch_t a = create_patch(32);
    endpt_t up_a = create_endpt(a, UPSTREAM);
    endpt_t down_a = create_endpt(a, DOWNSTREAM);
    patch_t b = create_patch(32);
    endpt_t up_b = create_endpt(b, UPSTREAM);
    endpt_t down_b = create_endpt(b, DOWNSTREAM);
    uuid_t id = router_attach(router, up_a);
    router_attach(router, up_b);

    printf("Sending a packet to '%s'\n", uuid_to_string(id, true));
    pckt_t packet = create_pckt();
    pckt_put(packet, "target", (data_t)&id, sizeof(uuid_t));
    pckt_put(packet, "message", "Hello, world!", sizeof("Hello, world!"));
    endpt_push(down_b, packet);

    pckt_t pull = (pckt_t)endpt_pull(down_a);
    char *message = pckt_get(pull, "message", NULL);
    printf("Pulled something down: '%s'\n", message);

    free_pckt(packet);
    free_patch(a);
    free_patch(b);
    free_router(router);

    return 0;
}