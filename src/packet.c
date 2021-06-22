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
    // Initially, the packet is empty
    result->header.num_parts = 0;

    table_t table = create_table();
    pckt_put(result, "route", (void *)&table);

    return result;
}

pckt_t parse_packet(data_t data)
{
    // Parse the packet header
    pckt_t result = create_pckt();
    if (data[0] != 'm' || data[1] != 'y') return result;
    result->header = *(phdr_t *)data;

    // Store received hash and calculate hash of received data
    hash_t hash_rec = result->header.body_hash;
    pckt_hash(result, &data[sizeof(phdr_t)], result->header.body_size);
    // Validate the packet body
    if (hash_rec != result->header.body_hash)
    {
        printf("Packet hashes do not match; discarding data");
        return result;
    }

    //TODO

    return result;
}

data_t pckt_encode(pckt_t packet, length_t *size)
{
    //TODO
    
    //pckt_hash(packet, data, size);
    //packet->header.body_size = ;
    *size = (length_t)sizeof(phdr_t) + packet->header.body_size;
    data_t buffer = (data_t)malloc(*size);

    return buffer;
}

data_t pckt_get(pckt_t packet, char *name, length_t *size)
{
    data_t data;
    hash_get(packet->manifest, name, (void **)&data, NULL);

    return data;
}

void pckt_put(pckt_t packet, char *name, void *data)
{
    packet->header.num_parts++;
    hash_put(packet->manifest, name, data, NULL);
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

hash_t pckt_hash(pckt_t packet, data_t data, length_t size)
{
    packet->header.body_hash = crc32c(0, data, size);

    return packet->header.body_hash;
}

void free_pckt(pckt_t packet)
{
    // Deep free
    free_table(packet->manifest);
    // Free buffer
    free(packet);
}