#include "packet.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

pckt_t create_pckt()
{
    pckt_t result = (pckt_t)malloc(sizeof(struct pckt_t));

    result->manifest = create_table();

    // Magic number (sanity check)
    result->header._secret_m = 'm';
    result->header._secret_y = 'y';
    // Header size and hash
    result->header.body_size = 0;
    result->header.body_hash = 0;
    // Initially, the packet is empty
    result->header.num_parts = 0;

    table_t table = create_table();
    pckt_put(result, "route", (data_t)&table, sizeof (void *));

    return result;
}

pckt_t parse_packet(data_t data)
{
    pckt_t result = create_pckt();

    //TODO

    return result;
}

data_t pckt_encode(pckt_t packet)
{
    //TODO

    return NULL;
}

data_t pckt_part(pckt_t packet, int start, length_t size)
{
    // Create buffer and copy partition
    data_t copy = (data_t)malloc(size);
    memcpy(copy, &packet->body_data[start], size);

    return copy;
}

data_t pckt_get(pckt_t packet, char *name, length_t *size)
{
    data_t data;
    data_t size_a;
    hash_get(packet->manifest, name, (void **)&data, (void **)&size_a);

    length_t temp = 0;
    if (size == NULL) size = &temp;
    *size = (length_t)((long)size_a);

    return pckt_part(packet, (long)data, (long)*size);
}

void pckt_put(pckt_t packet, char *name, data_t data, length_t size)
{
    packet->body_data = (data_t)realloc(packet->body_data, packet->header.body_size + size);
    unsigned int offset = packet->header.body_size;
    memcpy(&packet->body_data[offset], data, size);
    packet->header.body_size += size;
    packet->header.num_parts++;

    pckt_hash(packet);
    hash_put(packet->manifest, name, (void *)((long)offset), (void *)((long)size));
}

#define POLY 0x82f63b78

hash_t crc32c(hash_t crc, data_t data, length_t size)
{
    int k;
    crc = ~crc;

    while (size--)
    {
        crc ^= *data++;
        for (k = 0; k < 8; k++)
            crc = crc & 1 ? (crc >> 1) ^ POLY : crc >> 1;
    }

    return ~crc;
}

hash_t pckt_hash(pckt_t packet)
{
    packet->header.body_hash = crc32c(0, packet->body_data, packet->header.body_size);

    return packet->header.body_hash;
}

void free_pckt(pckt_t packet)
{
    // Deep free
    free(packet->body_data);
    // Free buffer
    free(packet);
}