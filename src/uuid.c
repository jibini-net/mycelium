#include "uuid.h"

#include <stdlib.h>
#include <sys/time.h>

// Useful character array for decimal to hex conversion
#define HEX_DIGITS "0123456789abcdef"

// Global static value as random is only seeded once per run
bool seeded = false;

uuid_t create_uuid()
{
    uuid_t result = 0;

    if (!seeded)
    {
        // Seed with the current time
        struct timeval t1;
        gettimeofday(&t1, NULL);
        srand(t1.tv_usec * t1.tv_sec);

        seeded = true;
    }

    // Generate 4 random integers (128 bits)
    int i = 0;
    for (i; i < 4; i++)
    {
        result <<= 32;
        result |= rand();
    }

    return result;
}

char *uuid_to_string(uuid_t uuid, bool hyphens)
{
    // Calculate size with or without hyphens
    short size = 33 + (hyphens ? 4 : 0);
    // Allocate result space
    char *result = (char *)malloc(size);

    // Subtract one for index and one for null-termination
    int i = size - 2;
    for (i; i >= 0; i--)
    {
        if (hyphens)
            switch(i)
            {
                // Special indices where hyphens are inserted
                case 8:
                case 13:
                case 18:
                case 23:
                    result[i] = '-';
                    continue;
            }

        // Convert from decimal (base-10) to hex (base-16)
        result[i] = HEX_DIGITS[uuid % 16];
        uuid /= 16;
    }

    // Null-terminate the string
    result[size - 1] = '\0';

    return result;
}

uuid_table_t create_uuid_table()
{
    // Dynamically allocate array of associations
    uuid_table_t result = (uuid_table_t)malloc(sizeof(struct uuid_assoc_t) * HASH_TABLE_SIZE);
    // Make sure all are null-terminated chains
    int i = 0;
    for (i; i < HASH_TABLE_SIZE; i++)
        result[i].next = NULL;

    return result;
}

void free_uuid_table(uuid_table_t table)
{
    int i = 0;
    for (i; i < HASH_TABLE_SIZE; i++)
    {
        struct uuid_assoc_t *temp = table[i].next;

        while (temp != NULL)
        {
            // Maintain reference for deletion
            struct uuid_assoc_t *d = temp;
            temp = temp->next;
            // Deletion
            free(d);
        }
    }

    // Free table's association array
    free(table);
}

void table_put(uuid_table_t table, uuid_t key, uuid_t value)
{
    unsigned int hash = key % HASH_TABLE_SIZE;
    // Allocate new node
    struct uuid_assoc_t *created = (struct uuid_assoc_t *)malloc(sizeof(struct uuid_assoc_t));
    // Grab hash chain
    struct uuid_assoc_t *chain = &table[hash];

    // Insert at start of chain (effectively overwrites existing values)
    created->next = chain->next;
    created->key = key;
    created->value = value;
    // Modify chain to point to inserted link
    chain->next = created;
}

uuid_t table_get(uuid_table_t table, uuid_t key)
{
    unsigned int hash = key % HASH_TABLE_SIZE;
    struct uuid_assoc_t *temp = &table[hash];

    while (temp->next != NULL)
    {
        temp = temp->next;
        if (temp->key == key)
            return temp->value;
    }

    return (uuid_t)0;
}