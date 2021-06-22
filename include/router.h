#pragma once

#include <stdbool.h>
#include <threads.h>

#include "packet.h"
#include "uuid.h"
#include "patch.h"

struct router_t
{
    uuid_t uuid;

    table_t route_table;
    table_t attachments;

    bool alive;
    thrd_t thread;
};
typedef struct router_t *router_t;

router_t create_router();

void free_router(router_t router);

uuid_t router_attach(router_t router, endpt_t endpoint);