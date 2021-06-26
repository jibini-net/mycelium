#include "packet.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void create_pckt(pckt_t *packet)
{
    create_table(&packet->manifest);

    // Magic number (sanity check)
    packet->header._secret_m = 'm';
    packet->header._secret_y = 'y';
    // Initially, the packet is empty
    packet->header.num_parts = 0;
    packet->header.body_size = 0;
    packet->header.body_hash = 0;
    // Initialize body to null
    packet->body = NULL;
}

void *pckt_get(pckt_t *packet, char *name, size_t *size)
{
    void *addr;
    hash_get(&packet->manifest, name, (void **)&addr, (void **)size);

    return &packet->body[(unsigned long)addr];
}

void pckt_put(pckt_t *packet, char *name, char *data, size_t size)
{
    packet->body = (char *)realloc(packet->body, packet->header.body_size + size);
    void *copy_to = &packet->body[packet->header.body_size];

    memcpy(copy_to, data, size);
    hash_put(&packet->manifest, name, (void *)packet->header.body_size, (void *)size);
    packet->header.body_size += size;
    packet->header.num_parts++;

    pckt_hash(packet);
}

#define POLY 0x82f63b78

hash_t crc32c(hash_t crc, char *data, size_t size)
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

hash_t pckt_hash(pckt_t *packet)
{
    packet->header.body_hash = crc32c(0, packet->body, packet->header.body_size);

    return packet->header.body_hash;
}

void free_pckt(pckt_t *packet)
{
    // Deep free
    free_table(&packet->manifest);

    if (packet->body != NULL)
    {
        free(packet->body);
        packet->body = NULL;
    }
}