#pragma once

#include "packet.h"
#include "uuid.h"
#include "patch.h"

struct router_t
{
    uuid_t uuid;

    uuid_table_t route_table;
    uuid_table_t attachments;
};
typedef struct router_t *router_t;

router_t create_router();

void free_router(router_t router);

uuid_t router_attach(router_t router, endpt_t endpoint);

void router_send(router_t router, pckt_t packet);