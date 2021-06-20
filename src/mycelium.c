
#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>

#include <semaphore.h>
#include <threads.h>

#include "packet.h"
#include "uuid.h"

int main(int args_c, char **args)
{
    // Create new packet
    pckt_t *packet = create_pckt();
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


    return 0;
}