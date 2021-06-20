#include "packet.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

pckt_t create_pckt()
{
    pckt_t result = (pckt_t)malloc(sizeof(struct pckt_t));

    // Magic number (sanity check)
    result->packet_header._secret_m = 'm';
    result->packet_header._secret_y = 'y';
    // Header size and hash
    result->packet_header.packet_size = 0;
    result->packet_header.packet_hash = 0;
    // Initially, the packet is empty
    result->packet_header.num_parts = 0;
}

pckt_data_t pckt_part(pckt_t packet, int start, pckt_size_t size)
{
    // Create buffer and copy partition
    pckt_data_t copy = (pckt_data_t)malloc(size);
    memcpy(copy, &packet->packet_data[start], size);

    return copy;
}

pckt_data_t pckt_get(pckt_t packet, char *name, pckt_size_t *size)
{
    //TODO READ MANIFEST

    pckt_size_t temp = 0;
    if (size == NULL) size = &temp;
    *size = packet->packet_header.packet_size;

    pckt_data_t copy = (pckt_data_t)malloc(*size);
    memcpy(copy, packet->packet_data, *size);

    return copy;
}

void pckt_put(pckt_t packet, char *name, pckt_data_t data, pckt_size_t size)
{
    packet->packet_data = (pckt_data_t)realloc(packet->packet_data, packet->packet_header.packet_size + size);
    memcpy(&packet->packet_data[packet->packet_header.packet_size], data, size);
    packet->packet_header.packet_size += size;

    //TODO UPDATE MANIFEST
}

pckt_hash_t pckt_hash(pckt_t packet)
{
    //TODO
    return 0;
}

void free_pckt(pckt_t packet)
{
    // Deep free
    free(packet->packet_data);
    // Free buffer
    free(packet);
}