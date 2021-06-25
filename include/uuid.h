#pragma once

#include <stdlib.h>
#include <stdbool.h>
#include <stdint.h>

// Standard UUIDs are stored as 128-bit unsigned integers
typedef __uint128_t uuid_t;
// Consumer type for iterating through tables
typedef void (*table_it_fun)(uuid_t, uuid_t);

#define HASH_TABLE_SIZE 1999

/**
 * @return Randomized and unique identifier (within the bounds of a
 *      cryptographically secure random generator).
 */
uuid_t random_uuid();

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
struct assoc_t
{
    // Key UUID with which the value is associated
    uuid_t key;
    // Value UUID which is associated with the key
    uuid_t value;

    // Next association key-value pair
    struct assoc_t *next;
};
typedef struct assoc_t assoc_t;

// A hash-table structure with associations of UUIDs with other UUIDs
struct table_t
{
    assoc_t data[HASH_TABLE_SIZE];
};
typedef struct table_t table_t;

/**
 * @param table Table which will be created with empty associations.
 */
void create_table(table_t *table);

/**
 * @return Deep frees the linked-lists within the provided table.
 */
void free_table(table_t*table);

/**
 * Places a hashed association between the key and value UUID in the table.
 * 
 * @param table UUID table to add associations for key and value.
 * @param key UUID key associated with the provided value.
 * @param value UUID value associated with the provided key.
 */
void table_put(table_t *table, uuid_t key, uuid_t value);

/**
 * @param data String from which to generate a hash.
 * @return A likely non-unique hash (always check that strings equal).
 */
uuid_t _quick_str_hash(char *data);

// Allows storage of strings and pointers as UUIDs
#define _ptr_to_uuid(a, b) ((uuid_t)((long)b) << 64) | (uuid_t)((long)a)
#define _uuid_to_ptr_a(uuid) (void *)((long)uuid)
#define _uuid_to_ptr_b(uuid) (void *)((long)(uuid >> 64))

/**
 * Retrieves the associated UUID value for the provided key.
 * 
 * @param table UUID table with associations for finding the value.
 * @param key UUID key associated with the requested value.
 * 
 * @return UUID value associated with the provided key UUID.
 */
uuid_t table_get(table_t *table, uuid_t key);

/**
 * Places the provided pointer values in the map in association with the
 * provided string name; a hash table can hold associations between one name and
 * two pointers.
 * 
 * @param table Table in which to add the association.
 * @param key String key of key-value pair.
 * @param a First pointer value of key-value pair.
 * @param b Second pointer value of key-value pair.
 */
void hash_put(table_t *table, char *key, void *a, void *b);

/**
 * Finds a pointer in the provided map associated with the provided key. Pointer
 * values will be stored in the referenced pointers (a and b).
 * 
 * @param table Table in which to find the association.
 * @param key String key of key-value pair.
 * @param a First pointer value of the requested key-value pair.
 * @param b Second pointer value of the requested key-value pair.
 */
void hash_get(table_t *table, char *key, void **a, void **b);

/**
 * Iterates over the table and calls the provided bi-consumer function.
 * 
 * @param table Table over which to iterate.
 * @param function Bi-consumer function called for each iteration.
 */
void table_it(table_t *table, table_it_fun function);