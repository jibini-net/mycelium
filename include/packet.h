#pragma once

#include "uuid.h"

// Numerical packet value types
typedef unsigned int hash_t;
typedef unsigned int addr_t;

// Packet header and hash data
struct phdr_t
{
    // Packet magic number (sanity check)
    char _secret_m;
    char _secret_y;
    // Header size and hash data
    size_t body_size;
    hash_t body_hash;
    // Header number of parts in packet
    unsigned int num_parts;
};
typedef struct phdr_t phdr_t;

// Packet header and body type
struct pckt_t
{
    // Packet's header data; contains hash and size data
    phdr_t header;
    // Manifest hash table for parts
    table_t manifest;
    // Dynamically allocated block of body memory
    char *body;
};
typedef struct pckt_t pckt_t;

/**
 * @param packet Packet to create with default initialized values.
 */
void create_pckt(pckt_t *packet);

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
void *pckt_get(pckt_t *packet, char *name, size_t *size);

/**
 * Adds the provided data to the packet's body and registers it in the packet's
 * header manifest.
 * 
 * @param packet Pointer to the packet to which data will be added.
 * @param name Name associated with the data in the packet.
 * @param data Pointer to place in the packet as a named part.
 * @param size Size of the data range which is being added.
 */
void pckt_put(pckt_t *packet, char *name, char *data, size_t size);

/**
 * Calculates a hash value to verify or compare packets' contents.
 * 
 * @param packet Packet whose header to write updated information.
 * 
 * @return Calculated hash bytes for comparison or tracking.
 */
hash_t pckt_hash(pckt_t *packet);

/**
 * @param packet Packet whose contents and data will be freed.
 */
void free_pckt(pckt_t *packet);