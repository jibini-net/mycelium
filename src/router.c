#include "router.h"

#include "packet.h"

router_t create_router()
{
    router_t result = (router_t)malloc(sizeof(struct router_t));
    result->uuid = create_uuid();

    result->route_table = create_table();
    result->attachments = create_table();

    return result;
}

void free_router(router_t router)
{
    free_table(router->route_table);
    free_table(router->attachments);

    free(router);
}

uuid_t router_attach(router_t router, endpt_t endpoint)
{
    uuid_t uuid = create_uuid();
    table_put(router->attachments, uuid, (uuid_t)((long)&endpoint));

    printf("Router '%s' has new attachment at '%s'\n",
        uuid_to_string(router->uuid, true),
        uuid_to_string(uuid, true));

    return uuid;
}

void router_send(router_t router, pckt_t packet)
{

}