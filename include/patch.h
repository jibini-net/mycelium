#pragma once

#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>

#include <semaphore.h>
#include <string.h>

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
typedef struct tube_t *tube_t;

/**
 * @param buffer_size Max number of pushed pointers can be buffered.
 * 
 * @return Created tube with an empty buffer.
 */
tube_t create_tube(size_t buffer_size);

/**
 * @param tube Tube whose memory will be freed.
 */
void free_tube(tube_t tube);

/**
 * Pushes a pointer onto the queue buffer which can then be pulled from the
 * other side.
 * 
 * @param tube Tube through which data will be pushed.
 * @param ptr Pointer to push through the tube.
 */
void tube_push(tube_t tube, void *ptr);

/**
 * Waits for and pulls a pointer from the queue buffer once one is available.
 * 
 * @param tube Tube through which data is received.
 * 
 * @return Pulled pointer from the queue buffer.
 */
void *tube_pull(tube_t tube);