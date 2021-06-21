#include "patch.h"

tube_t create_tube(size_t buffer_size)
{
    tube_t result = (tube_t)malloc(sizeof(struct tube_t));

    sem_init(&result->open_slots, 0, buffer_size);
    sem_init(&result->available, 0, 0);
    sem_init(&result->mutex, 0, 1);

    result->index_in = 0;
    result->index_out = 0;

    result->buffer = (void **)malloc(sizeof(void *) * buffer_size);
    result->buffer_size = buffer_size;
}

void free_tube(tube_t tube)
{
    // Free semaphores
    sem_destroy(&tube->open_slots);
    sem_destroy(&tube->available);
    sem_destroy(&tube->mutex);

    // Deep free
    free(tube->buffer);
    free(tube);
}

void tube_push(tube_t tube, void *ptr)
{
    sem_wait(&tube->open_slots);
    sem_wait(&tube->mutex);

    tube->buffer[tube->index_in++] = ptr;
    tube->index_in %= tube->buffer_size;

    sem_post(&tube->mutex);
    sem_post(&tube->available);
}

void *tube_pull(tube_t tube)
{
    sem_wait(&tube->available);
    sem_wait(&tube->mutex);

    void *result = tube->buffer[tube->index_out];
    tube->buffer[tube->index_out++] = NULL;
    tube->index_out %= tube->buffer_size;

    sem_post(&tube->mutex);
    sem_post(&tube->open_slots);

    return result;
}

patch_t create_patch(size_t buffer_size)
{
    patch_t result = (patch_t)malloc(sizeof(struct patch_t));
    result->tube_a = create_tube(buffer_size);
    result->tube_b = create_tube(buffer_size);

    return result;
}

void free_patch(patch_t patch)
{
    free_tube(patch->tube_a);
    free_tube(patch->tube_b);

    free(patch);
}

endpt_t create_endpt(patch_t patch, up_down_t up_down)
{
    endpt_t result;
    result.patch = patch;
    result.up_down = up_down;

    return result;
}

void *endpt_pull(endpt_t endpoint)
{
    switch (endpoint.up_down)
    {
        case UPSTREAM:
            return tube_pull(endpoint.patch->tube_a);
        case DOWNSTREAM:
            return tube_pull(endpoint.patch->tube_b);
    }
}

void endpt_push(endpt_t endpoint, void *ptr)
{
    switch (endpoint.up_down)
    {
        case UPSTREAM:
            tube_push(endpoint.patch->tube_b, ptr);
            return;
        case DOWNSTREAM:
            tube_push(endpoint.patch->tube_a, ptr);
    }
}