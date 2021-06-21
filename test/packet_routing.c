
#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>

#include <semaphore.h>
#include <threads.h>

#include "packet.h"
#include "uuid.h"
#include "patch.h"
#include "router.h"

int test_packet();
int test_uuid();
int test_uuid_table();
int test_hash_table();
int test_tube();
int test_patch();
int test_router();

#define TEST_BOUNDARY(append) printf("=================================%s\n", append)
#define TEST(name, task, fail) TEST_BOUNDARY("");\
    printf("Running test: '%s'\n---------------------------------\n", name);\
    *fail += task();\
    TEST_BOUNDARY("\n");

int main(int args_c, char **args)
{
    int fail = 0;

    TEST("Packet put and retrieve", test_packet, &fail);
    TEST("UUID generation and printing", test_uuid, &fail);
    TEST("UUID hash table associations", test_uuid_table, &fail);
    TEST("UUID hash table (named pointers)", test_hash_table, &fail);
    TEST("Tube send and receive", test_tube, &fail);
    TEST("Patch upstream and downstream", test_patch, &fail);
    TEST("Router attachment and route", test_router, &fail);

    return fail;
}

int test_packet()
{
    // Create new packet
    pckt_t packet = create_pckt();
    // Put test data as data partitions
    pckt_put(packet, "foo", "Hello, world!", sizeof("Hello, world!") - sizeof(""));
    printf("Packet size: %u\n", packet->packet_header.packet_size);
    pckt_put(packet, "bar", "Bin, baz", sizeof("Bin, baz") - sizeof(""));
    printf("Packet size: %u\n", packet->packet_header.packet_size);

    // Retrieve and print partitions
    char *foo = pckt_get(packet, "foo", NULL);
    char *bar = pckt_get(packet, "bar", NULL);
    printf("foo -> '%s'\nbar -> '%s'\n", foo, bar);
    // Free packet memory and partitions
    free(foo);
    free(bar);
    free_pckt(packet);
}

int test_uuid()
{
    int i = 0;
    for (i; i < 4; i++)
    {
        // Create new random UUID
        uuid_t uuid = create_uuid();

        // Convert the UUID to hex string
        char *str = uuid_to_string(uuid, true);
        printf("UUID (%d): %s\n", i, str);
        // Free stringified UUID memory
        free(str);
    }
}

int test_uuid_table()
{
    uuid_table_t table = create_uuid_table();

    table_put(table, 0, 1);
    table_put(table, 1, 2);
    table_put(table, 2, 0);

    uuid_t a = table_get(table, 0);
    uuid_t b = table_get(table, 1);
    uuid_t c = table_get(table, 2);
    uuid_t n = table_get(table, 3);
    printf("0 -> %d, 1 -> %d, 2 -> %d, 3 -> %d\n", (int)a, (int)b, (int)c, (int)n);

    free_uuid_table(table);
}

int test_hash_table()
{
    uuid_table_t table = create_uuid_table();

    hash_put(table, "a", "b");
    hash_put(table, "b", "c");
    hash_put(table, "c", "a");

    char *a = (char *)hash_get(table, "a");
    char *b = (char *)hash_get(table, "b");
    char *c = (char *)hash_get(table, "c");
    printf("a -> %s, b -> %s, c -> %s\n", a, b, c);

    free_uuid_table(table);
}

int test_tube()
{
    char *a_str = "A";
    char *b_str = "B";
    char *c_str = "C";

    tube_t tube = create_tube(32);

    tube_push(tube, a_str);
    tube_push(tube, b_str);
    tube_push(tube, c_str);

    printf("%s\n", (char *)tube_pull(tube));
    printf("%s\n", (char *)tube_pull(tube));
    printf("%s\n", (char *)tube_pull(tube));

    free_tube(tube);
}

int test_patch()
{
    // Create a patch
    patch_t patch = create_patch(32);
    // Create both ends of the patch
    endpt_t upstream = create_endpt(patch, UPSTREAM);
    endpt_t downstream = create_endpt(patch, DOWNSTREAM);

    // Test sending things downstream
    char *test_str = "Hello, world!";
    endpt_push(upstream, test_str);
    char *str = (char *)endpt_pull(downstream);
    printf("From patch endpoints: '%s'\n", str);
    // Test sending things upstream
    test_str = "Foo, bar";
    endpt_push(downstream, test_str);
    str = (char *)endpt_pull(upstream);
    printf("From patch endpoints: '%s'\n", str);
    
    // Free the patch
    free_patch(patch);
}

int test_router()
{
    // Create router and patch
    router_t router = create_router();
    patch_t patch = create_patch(32);
    // Get both ends of patch
    endpt_t upstream = create_endpt(patch, UPSTREAM);
    endpt_t downstream = create_endpt(patch, DOWNSTREAM);
    // Router should be considered upstream
    uuid_t uuid = router_attach(router, upstream);

    printf("Attached at '%s'\n", uuid_to_string(uuid, true));

    free_router(router);
}