#pragma once

#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>

#include <semaphore.h>
#include <string.h>

#include "packet.h"

// Allows a producer-consumer relationship between two processes
struct tube_t
{
    // Synchronization primitives for buffer space
    sem_t open_slots;
    sem_t available;
    // Mutual exclusion of the indices and buffer
    sem_t mutex;

    // Next indices for input and output on the buffer
    volatile int index_in;
    volatile int index_out;
    // Buffer memory and its size (for wrapping)
    void **buffer;
    size_t buffer_size;
};
typedef struct tube_t tube_t;

/**
 * @param tube Tube to created with an empty buffer.
 * @param buffer_size Max number of pushed pointers can be buffered.
 */
void create_tube(tube_t *tube, size_t buffer_size);

/**
 * @param tube Tube whose memory will be freed.
 */
void free_tube(tube_t *tube);

/**
 * Pushes a pointer onto the queue buffer which can then be pulled from the
 * other side.
 * 
 * @param tube Tube through which data will be pushed.
 * @param data Pointer to push through the tube.
 */
void tube_push(tube_t *tube, void *data);

/**
 * Waits for and pulls a pointer from the queue buffer once one is available.
 * 
 * @param tube Tube through which data is received.
 * 
 * @return Pulled pointer from the queue buffer.
 */
void *tube_pull(tube_t *tube);

/**
 * @param tube Tube to check for pushed elements.
 * 
 * @return Whether there are elements waiting to be pulled in the provided tube.
 */
bool tube_peek(tube_t *tube);

// Allows duplex communication between two processes
struct patch_t
{
    tube_t tube_a;
    tube_t tube_b;
};
typedef struct patch_t patch_t;

/**
 * @param patch Patch instance to created and initialize.
 * @param buffer_size Internal buffer size of the patch.
 */
void create_patch(patch_t *patch, size_t buffer_size);

/**
 * @param patch Patch to destroy and free.
 */
void free_patch(patch_t *patch);

typedef int up_down_t;
#define UPSTREAM 0
#define DOWNSTREAM 1

/**
 * A duplex endpoint in a patch's communication. Like a tube, it can be pulled
 * and pushed; unlike a tube, pushes send to an external entity and pulls come
 * from external pushers.
 */
struct endpt_t
{
    patch_t *patch;
    up_down_t up_down;
};
typedef struct endpt_t endpt_t;

/**
 * @param patch Patch instance from which to create a endpoint handle.
 * @param up_down Whether this endpoint is upstream or downstream.
 * 
 * @return Created endpoint instance to interact with the provided patch.
 */
endpt_t patch_endpt(patch_t *patch, up_down_t up_down);

/**
 * Waits for and returns a pointer from the provided endpoint.
 * 
 * @param endpoint Endpoint on which to wait for pushed elements.
 * 
 * @return Received pointer from the provided endpoint.
 */
void *endpt_pull(endpt_t endpoint);

/**
 * Sends a provided pointer through an endpoint.
 * 
 * @param endpoint Endpoint through which to push the data.
 * @param data Pointer to push through the endpoint.
 */
void endpt_push(endpt_t endpoint, void *data);

/**
 * @param endpoint Endpoint to check for pushed elements.
 * 
 * @return Whether there are elements waiting to be pulled.
 */
bool endpt_peek(endpt_t endpoint);