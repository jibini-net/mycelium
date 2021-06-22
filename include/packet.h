#pragma once

#include "uuid.h"

// Numerical packet value types
typedef unsigned int length_t;
typedef unsigned int hash_t;
typedef unsigned int addr_t;
// Data range in memory type
typedef char *data_t;

// Packet header and hash data
struct phdr_t
{
    // Packet magic number (sanity check)
    char _secret_m;
    char _secret_y;
    // Header size and hash data
    length_t body_size;
    hash_t body_hash;
    // Header number of parts in packet
    unsigned int num_parts;
};
typedef struct phdr_t phdr_t;

// Packet header and body type
struct pckt_t
{
    // Packet's header data
    phdr_t header;
    table_t manifest;
    // Packet's body data
    data_t body_data;
};
typedef struct pckt_t *pckt_t;

/**
 * @return Created empty packet with default initialized values.
 */
pckt_t create_pckt();

/**
 * Parses the provided data range and creates a mutable packet.
 * 
 * @param data Data range containing the packet header and body.
 * 
 * @return Created packet from the provided data in memory.
 */
pckt_t parse_packet(data_t data);

/**
 * Encodes the provided packet to a contiguous data range.
 * 
 * @param packet Packet whose data to encode.
 * 
 * @return Allocated space in memory containing the packet data.
 */
data_t pckt_encode(pckt_t packet);

/**
 * Partitions the provided packet's body to a cropped range of its data.
 * 
 * @param packet Pointer to the packet whose data to partition.
 * @param start Relative start address of the data partition.
 * @param size Size of the data contained within the data partition.
 * 
 * @return Partitioned data as a generic partition pointer type.
 */
data_t pckt_part(pckt_t packet, int start, length_t size);

/**
 * Reads the provided packet's header to find and partition data associated with
 * the provided name.
 * 
 * @param packet Pointer to the packet whose data to partition.
 * @param name Name associated with the data in the packet.
 * @param size Size of the data partition is written to the referenced field; if
 *      not required, use 'NULL' or zero.
 * 
 * @return Partitioned data as a generic partition pointer type.
 */
data_t pckt_get(pckt_t packet, char *name, length_t *size);

/**
 * Adds the provided data to the packet's body and registers it in the packet's
 * header manifest.
 * 
 * @param packet Pointer to the packet to which data will be added.
 * @param name Name associated with the data in the packet.
 * @param size Size of the data provided as number of bytes.
 */
void pckt_put(pckt_t packet, char *name, data_t data, length_t size);

/**
 * Calculates a hash value to verify or compare packets' contents.
 * 
 * @param packet Packet with whose body contents to calculate a hash.
 * 
 * @return Calculated hash bytes for comparison or tracking.
 */
hash_t pckt_hash(pckt_t packet);

/**
 * @param packet Packet whose contents and data will be freed.
 */
void free_pckt(pckt_t packet);