
#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>

#include <semaphore.h>
#include <threads.h>

#include "packet.h"
#include "uuid.h"
#include "patch.h"

void test_packet();
void test_uuid();
void test_uuid_table();
void test_tube();
void test_patch();

int main(int args_c, char **args)
{
    test_packet();
    test_uuid();
    test_uuid_table();
    test_tube();
    test_patch();

    return 0;
}

void test_packet()
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

void test_uuid()
{
    int i = 0;
    for (i; i < 4; i++)
    {
        // Create new random UUID
        uuid_t uuid = create_uuid();

        // Convert the UUID to hex string
        char *str = uuid_to_string(uuid, true);
        printf("UUID (%02d): %s\n", i, str);
        // Free stringified UUID memory
        free(str);
    }
}

void test_uuid_table()
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

void test_tube()
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

void test_patch()
{
    // Create a patch
    patch_t patch = create_patch(32);
    // Create both ends of the patch
    endpt_t upstream = create_endpt(patch, UPSTREAM);
    endpt_t downstream = create_endpt(patch, DOWNSTREAM);

    // Test sending things downstream
    char *test_str = "Hello, world!";
    endpt_push(upstream, test_str);
    char *str = endpt_pull(downstream);
    printf("From patch endpoints: '%s'\n", str);
    // Test sending things upstream
    test_str = "Foo, bar";
    endpt_push(downstream, test_str);
    str = endpt_pull(upstream);
    printf("From patch endpoints: '%s'\n", str);
    
    // Free the patch (a)
    free_patch(patch);
}