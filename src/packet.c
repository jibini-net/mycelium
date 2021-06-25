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

    table_t table;
    create_table(&table);
    pckt_put(packet, "route", (void *)&table);
}

void parse_packet(pckt_t *packet, data_t data)
{
    // Parse the packet header
    create_pckt(packet);
    if (data[0] != 'm' || data[1] != 'y') return;
    packet->header = *(phdr_t *)data;

    // Store received hash and calculate hash of received data
    hash_t hash_rec = packet->header.body_hash;
    pckt_hash(packet, &data[sizeof(phdr_t)], packet->header.body_size);
    // Validate the packet body
    if (hash_rec != packet->header.body_hash)
    {
        printf("Packet hashes do not match; discarding data");
        return;
    }

    //TODO
}

data_t pckt_encode(pckt_t *packet, length_t *size)
{
    //TODO
    
    //pckt_hash(packet, data, size);
    //packet->header.body_size = ;
    *size = (length_t)sizeof(phdr_t) + packet->header.body_size;
    data_t buffer = (data_t)malloc(*size);

    return buffer;
}

void *pckt_get(pckt_t *packet, char *name, length_t *size)
{
    data_t data;
    hash_get(&packet->manifest, name, (void **)&data, NULL);

    return data;
}

void pckt_put(pckt_t *packet, char *name, void *data)
{
    hash_put(&packet->manifest, name, data, NULL);
    packet->header.num_parts++;
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

hash_t pckt_hash(pckt_t *packet, data_t data, length_t size)
{
    packet->header.body_hash = crc32c(0, data, size);

    return packet->header.body_hash;
}

void free_pckt(pckt_t *packet)
{
    // Deep free
    free_table(&packet->manifest);
}