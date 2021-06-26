#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>

#include "packet.h"
#include "uuid.h"
#include "patch.h"
#include "router.h"

int test_router_send();
int test_router_embed_rt();
int test_router_memory_reg();
int test_router_return();
int test_router_table();
int test_router_upstream();

#define TEST_BOUNDARY(append) printf("=================================%s\n", append)
#define TEST(name, task, fail) TEST_BOUNDARY("");\
    printf("Running test: '%s'\n---------------------------------\n", name);\
    *fail += task();\
    TEST_BOUNDARY("\n");

int main(int args_c, char **args)
{
    int fail = 0;

    TEST("Router packet send (sanity check)", test_router_send, &fail);
    TEST("Router packet embedded route test", test_router_embed_rt, &fail);
    TEST("Router memory regression under load test", test_router_memory_reg, &fail);
    TEST("Router packet return routing", test_router_return, &fail);
    TEST("Router packet with routing table", test_router_table, &fail);
    TEST("Router fallback to upstream route", test_router_upstream, &fail);

    return fail;
}

void _init_router_test(router_t *router,
    patch_t *a, patch_t *b,
    endpt_t *up_a, endpt_t *down_a,
    endpt_t *up_b, endpt_t *down_b,
    uuid_t *attach_a, uuid_t *attach_b)
{
    // Initialize router and patches
    create_router(router);
    create_patch(a, 32);
    create_patch(b, 32);
    // Create duplex endpoints for both patches
    *up_a = patch_endpt(a, UPSTREAM);
    *down_a = patch_endpt(a, DOWNSTREAM);
    *up_b = patch_endpt(b, UPSTREAM);
    *down_b = patch_endpt(b, DOWNSTREAM);
    // Attach upstreams of patches to router
    *attach_a = router_attach(router, *up_a);
    *attach_b = router_attach(router, *up_b);
    
    printf("Test router is running at ~%d Hz\n", 1000000000 / REFRESH_WAIT_NANO);
    printf("Test attachment 'A' is at '%s'\n", uuid_to_string(*attach_a, true));
    printf("Test attachment 'B' is at '%s'\n", uuid_to_string(*attach_b, true));
}

char *_test_router_send_base(router_t *router,
    patch_t *a, patch_t *b,
    endpt_t up_a, endpt_t down_a,
    endpt_t up_b, endpt_t down_b,
    uuid_t attach_a, uuid_t attach_b,
    pckt_t *packet)
{
    // Allocate and create new packet
    create_pckt(packet);

    // Create packet data (target to patch A)
    pckt_put(packet, "target", (void *)&attach_a, sizeof(uuid_t));
    pckt_put(packet, "message", "Hello, world!", sizeof("Hello, world!"));
    // Send data to patch A via router
    endpt_push(down_b, packet);

    // Pull the packet from A downstream from router
    pckt_t *pull = (pckt_t *)endpt_pull(down_a);
    // Get message sent from B
    return pckt_get(pull, "message", NULL);
}

int test_router_send()
{
    router_t router;
    patch_t a, b;
    endpt_t up_a, down_a;
    endpt_t up_b, down_b;
    uuid_t attach_a, attach_b;
    pckt_t packet;

    _init_router_test(&router, 
        &a, &b,
        &up_a, &down_a,
        &up_b, &down_b,
        &attach_a, &attach_b);
    char *message = _test_router_send_base(
        &router, 
        &a, &b,
        up_a, down_a,
        up_b, down_b,
        attach_a, attach_b,
        &packet);
    printf("Pulled something down: '%s'\n", message);

    // Free packet, patches, and router
    free_pckt(&packet);
    free_patch(&a);
    free_patch(&b);
    free_router(&router);
}

int test_router_embed_rt()
{
    router_t router;
    patch_t a, b;
    endpt_t up_a, down_a;
    endpt_t up_b, down_b;
    uuid_t attach_a, attach_b;
    pckt_t packet;

    _init_router_test(&router, 
        &a, &b,
        &up_a, &down_a,
        &up_b, &down_b,
        &attach_a, &attach_b);
    char *message = _test_router_send_base(
        &router, 
        &a, &b,
        up_a, down_a,
        up_b, down_b,
        attach_a, attach_b,
        &packet);
    printf("Pulled something down: '%s'\n", message);

    pckt_embed_rt(&packet);
    // Read 
    size_t rt_size;
    uuid_t *route = pckt_get(&packet, "route", &rt_size);
    unsigned int rt_c = rt_size / ROUTE_ROW_SIZE;

    int i = 0;
    for (i; i < rt_c; i += 1)
    {
        printf("'%s' -> '%s'\n", uuid_to_string(route[i * 2], true),
            uuid_to_string(route[i * 2 + 1], true));
    }

    // Free packet, patches, and router
    free_pckt(&packet);
    free_patch(&a);
    free_patch(&b);
    free_router(&router);
}

int test_router_memory_reg()
{
    router_t router;
    patch_t a, b;
    endpt_t up_a, down_a;
    endpt_t up_b, down_b;
    uuid_t attach_a, attach_b;
    pckt_t packet;

    _init_router_test(&router, 
        &a, &b,
        &up_a, &down_a,
        &up_b, &down_b,
        &attach_a, &attach_b);

    // 20-second shallow buffer stress test
    int i = 0;
    for (i; i < 5000; i++)
    {
        char *message = _test_router_send_base(
            &router, 
            &a, &b,
            up_a, down_a,
            up_b, down_b,
            attach_a, attach_b,
            &packet);
        free_pckt(&packet);
    }
    
    // Free packet, patches, and router
    free_patch(&a);
    free_patch(&b);
    free_router(&router);
}

int test_router_return()
{
    router_t router;
    patch_t a, b;
    endpt_t up_a, down_a;
    endpt_t up_b, down_b;
    uuid_t attach_a, attach_b;
    pckt_t packet;

    _init_router_test(&router, 
        &a, &b,
        &up_a, &down_a,
        &up_b, &down_b,
        &attach_a, &attach_b);
    create_pckt(&packet);

    table_put(&packet.route, router.uuid, attach_b);

    // Create packet data (target to patch A)
    printf("Sending a returnable packet to '%s'\n", uuid_to_string(attach_a, true));
    pckt_put(&packet, "target", (void *)&attach_a, sizeof(uuid_t));
    pckt_put(&packet, "message", "Hello, world!", sizeof("Hello, world!"));
    // Send data to patch A via router
    endpt_push(down_b, &packet);

    // Pull the packet from A downstream from router
    pckt_t *pull = (pckt_t *)endpt_pull(down_b);
    // Get message sent from B
    char *message = pckt_get(pull, "message", NULL);
    printf("Pulled something down from sender: '%s'\n", message);

    // Free packet, patches, and router
    free_pckt(&packet);
    free_patch(&a);
    free_patch(&b);
    free_router(&router);
}

int test_router_table()
{
    router_t router;
    patch_t a, b;
    endpt_t up_a, down_a;
    endpt_t up_b, down_b;
    uuid_t attach_a, attach_b;
    pckt_t packet;

    _init_router_test(&router, 
        &a, &b,
        &up_a, &down_a,
        &up_b, &down_b,
        &attach_a, &attach_b);
    create_pckt(&packet);

    uuid_t undeliverable = random_uuid();
    table_put(&router.route_table, undeliverable, attach_a);

    // Create packet data (target to patch A)
    printf("Sending a non-peer target packet to '%s'\n", uuid_to_string(undeliverable, true));
    pckt_put(&packet, "target", (void *)&undeliverable, sizeof(uuid_t));
    pckt_put(&packet, "message", "Hello, world!", sizeof("Hello, world!"));
    // Send data to patch A via router
    endpt_push(down_b, &packet);

    // Pull the packet from A downstream from router
    pckt_t *pull = (pckt_t *)endpt_pull(down_a);
    // Get message sent from B
    char *message = pckt_get(pull, "message", NULL);
    printf("Received packet routed to table entry: '%s'\n", message);

    // Free packet, patches, and router
    free_pckt(&packet);
    free_patch(&a);
    free_patch(&b);
    free_router(&router);
}

int test_router_upstream()
{
    router_t router;
    patch_t a, b;
    endpt_t up_a, down_a;
    endpt_t up_b, down_b;
    uuid_t attach_a, attach_b;
    pckt_t packet;

    _init_router_test(&router, 
        &a, &b,
        &up_a, &down_a,
        &up_b, &down_b,
        &attach_a, &attach_b);
    create_pckt(&packet);

    // Set patch endpoint as the upstream
    router.upstream = attach_a;

    uuid_t undeliverable = random_uuid();
    printf("Sending packet to unknown address '%s'\n", uuid_to_string(undeliverable, true));
    pckt_put(&packet, "target", (void *)&undeliverable, sizeof(uuid_t));
    pckt_put(&packet, "message", "Hello, world!", sizeof("Hello, world!"));
    // Send data to patch A via router
    endpt_push(down_b, &packet);

    // Pull the packet from A downstream from router
    pckt_t *pull = (pckt_t *)endpt_pull(down_a);
    // Get message sent from B
    char *message = pckt_get(pull, "message", NULL);
    printf("Received packet from simulated upstream: '%s'\n", message);

    // Free packet, patches, and router
    free_pckt(&packet);
    free_patch(&a);
    free_patch(&b);
    free_router(&router);
}