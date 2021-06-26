#include "packet.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <threads.h>

void create_pckt(pckt_t *packet)
{
    create_table(&packet->manifest);
    create_table(&packet->route);

    // Magic number (sanity check)
    packet->header._secret_m = 'm';
    packet->header._secret_y = 'y';
    // Initially, the packet is empty
    packet->header.num_parts = 0;
    packet->header.body_size = 0;
    packet->header.body_hash = 0;
    // Initialize body to null
    packet->body = NULL;
    packet->route_c = 0;
}

void *pckt_get(pckt_t *packet, char *name, size_t *size)
{
    // Read address (offset) and size of data in packet
    void *addr;
    hash_get(&packet->manifest, name, (void **)&addr, (void **)size);
    // Also return a pointer to that area in memory
    return &packet->body[(unsigned long)addr];
}

void pckt_put(pckt_t *packet, const char *name, char *data, size_t size)
{
    // Reallocate body buffer
    packet->body = (char *)realloc(packet->body, packet->header.body_size + size);
    // Copy data into appended capacity
    void *copy_to = &packet->body[packet->header.body_size];
    memcpy(copy_to, data, size);

    // Place a manifest entry
    hash_put(&packet->manifest, name, (void *)packet->header.body_size, (void *)size);
    // Update header data
    packet->header.body_size += size;
    packet->header.num_parts++;
    // Update header hash data
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
    // Update hash in header to 32-bit CRC
    packet->header.body_hash = crc32c(0, packet->body, packet->header.body_size);
    // Also return that hash (convenience)
    return packet->header.body_hash;
}

void free_pckt(pckt_t *packet)
{
    // Deep free
    free_table(&packet->manifest);
    free_table(&packet->route);

    // Free body; not-null asserted because of empty packets
    if (packet->body != NULL)
    {
        free(packet->body);
        packet->body = NULL;
    }
}

thread_local char *_pckt_rt_buff = NULL;
thread_local unsigned long _pckt_rt_index = 0;

// Iterator function for encoding route crumbs
void _pckt_rt_it(uuid_t router, uuid_t to)
{
    memcpy(&_pckt_rt_buff[_pckt_rt_index], &router, sizeof(uuid_t));
    _pckt_rt_index += sizeof(uuid_t);
    memcpy(&_pckt_rt_buff[_pckt_rt_index], &to, sizeof(uuid_t));
    _pckt_rt_index += sizeof(uuid_t);
}

void pckt_embed_rt(pckt_t *packet)
{
    // Set iteration environment
    _pckt_rt_buff = (char *)malloc(ROUTE_ROW_SIZE * packet->route_c);
    _pckt_rt_index = 0;

    // Iterate over route table
    table_it(&packet->route, (table_it_fun)_pckt_rt_it);
    // Embed the generated buffer into the packet
    pckt_put(packet, "route", _pckt_rt_buff, ROUTE_ROW_SIZE * packet->route_c);
    
    free(_pckt_rt_buff);
}

void pckt_load_rt(pckt_t *packet)
{
    // Read route table and get size
    size_t rt_size;
    uuid_t *route = pckt_get(packet, "route", &rt_size);
    // Calculate number of rows
    packet->route_c = rt_size / ROUTE_ROW_SIZE;

    // Iterate through all rows in table
    int i = 0;
    for (i; i < packet->route_c; i += 1)
        table_put(&packet->route, route[i * 2], route[i * 2 + 1]);
}