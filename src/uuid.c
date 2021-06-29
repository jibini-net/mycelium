#include "uuid.h"

#include <stdlib.h>
#include <string.h>

#include <sys/time.h>

// Useful character array for decimal to hex conversion
#define HEX_DIGITS "0123456789abcdef"

// Global static value as random is only seeded once per run
bool seeded = false;

uuid_t random_uuid()
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

void create_table(table_t *table)
{
    // Make sure all are null-terminated chains
    int i = 0;
    for (i; i < HASH_TABLE_SIZE; i++)
        table->data[i].next = NULL;
}

void free_table(table_t *table)
{
    int i = 0;
    for (i; i < HASH_TABLE_SIZE; i++)
    {
        struct assoc_t *temp = table->data[i].next;

        while (temp != NULL)
        {
            // Maintain reference for deletion
            struct assoc_t *d = temp;
            temp = temp->next;
            // Deletion
            free(d);
        }
    }
}

void table_put(table_t *table, uuid_t key, uuid_t value)
{
    unsigned int hash = key % HASH_TABLE_SIZE;
    // Allocate new node
    struct assoc_t *created = (struct assoc_t *)malloc(sizeof(struct assoc_t));
    // Grab hash chain
    struct assoc_t *chain = &table->data[hash];

    // Insert at start of chain (effectively overwrites existing values)
    created->next = chain->next;
    created->key = key;
    created->value = value;
    // Modify chain to point to inserted link
    chain->next = created;
}

uuid_t table_get(table_t *table, uuid_t key)
{
    unsigned int hash = key % HASH_TABLE_SIZE;
    struct assoc_t *temp = &table->data[hash];

    while (temp->next != NULL)
    {
        temp = temp->next;
        if (temp->key == key)
            return temp->value;
    }

    return (uuid_t)0;
}

uuid_t _quick_str_hash(char *data)
{
    uuid_t hash = (uuid_t)0;
    int i = 0, len = strlen(data);

    for (i; i < 16 && i < len; i++)
    {
        hash <<= 8;
        hash |= data[i];
    }

    return hash;
}

void hash_put(table_t *table, const char *key, void *a, void *b)
{
    unsigned int hash = _quick_str_hash((char *)key) % HASH_TABLE_SIZE;
    // Allocate new node
    struct assoc_t *created = (struct assoc_t *)malloc(sizeof(struct assoc_t));
    // Grab hash chain
    struct assoc_t *chain = &table->data[hash];

    // Insert at start of chain (effectively overwrites existing values)
    created->next = chain->next;
    created->key = _ptr_to_uuid(key, NULL);
    created->value = _ptr_to_uuid(a, b);
    // Modify chain to point to inserted link
    chain->next = created;
}

void hash_get(table_t *table, char *key, void **a, void **b)
{
    unsigned int hash = _quick_str_hash(key) % HASH_TABLE_SIZE;
    struct assoc_t *temp = &table->data[hash];

    int i = 0;
    while (temp->next != NULL)
    {
        temp = temp->next;
        
        if (strcmp(_uuid_to_ptr_a(temp->key), key) == 0)
        {
            if (a != NULL) *a = _uuid_to_ptr_a(temp->value);
            if (b != NULL) *b = _uuid_to_ptr_b(temp->value);
            
            return;
        }
    }

    if (a != NULL) *a = NULL;
    if (b != NULL) *b = NULL;
}

void table_it(table_t *table, table_it_fun function)
{
    int i = 0;
    for (i; i < HASH_TABLE_SIZE; i++)
    {
        struct assoc_t *temp = &table->data[i];

        while (temp->next != NULL)
        {
            temp = temp->next;
            function(temp->key, temp->value);
        }
    }
}