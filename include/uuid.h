#pragma once

#include <stdlib.h>
#include <stdbool.h>
#include <stdint.h>

// Standard UUIDs are stored as 128-bit unsigned integers
typedef __uint128_t uuid_t;

#define HASH_TABLE_SIZE 1999

/**
 * @return Randomized and unique identifier (within the bounds of a
 *      cryptographically secure random generator).
 */
uuid_t create_uuid();

/**
 * Converts the provided UUID to a hexidecimal string representation.
 * 
 * @param uuid UUID to convert to a hex string.
 * @param hyphens Set to true to include hyphens in UUID string.
 * 
 * @return Generated string with hexidecimal UUID.
 */
char *uuid_to_string(uuid_t uuid, bool hyphens);

// Single key-value association linked node
struct uuid_assoc_t
{
    // Key UUID with which the value is associated
    uuid_t key;
    // Value UUID which is associated with the key
    uuid_t value;

    // Next association key-value pair
    struct uuid_assoc_t *next;
};
// A hash-table structure with associations of UUIDs with other UUIDs
typedef struct uuid_assoc_t *uuid_table_t;

/**
 * @return Dynamically allocated empty association hash table.
 */
uuid_table_t create_uuid_table();

/**
 * @return Deep frees the linked-lists within the provided table.
 */
void free_uuid_table(uuid_table_t table);

/**
 * Places a hashed association between the key and value UUID in the table.
 * 
 * @param table UUID table to add associations for key and value.
 * @param key UUID key associated with the provided value.
 * @param value UUID value associated with the provided key.
 */
void table_put(uuid_table_t table, uuid_t key, uuid_t value);

/**
 * Retrieves the associated UUID value for the provided key.
 * 
 * @param table UUID table with associations for finding the value.
 * @param key UUID key associated with the requested value.
 * 
 * @return UUID value associated with the provided key UUID.
 */
uuid_t table_get(uuid_table_t table, uuid_t key);