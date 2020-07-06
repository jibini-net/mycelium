package net.jibini.mycelium.transfer

import net.jibini.mycelium.transfer.impl.RouterImpl
import java.util.*

/**
 * Allows several [attachments][AsyncPipe] to connect with addresses to exchange routed information; also can
 * [embed return addresses][embedReturns] so the messages can be returned to their sender in reversed order; when an
 * external service comes online, it can broadcast its status update and [register a static route][staticRoute] in
 * order for data to find its delivery target
 */
interface Router<T : Addressed>
{
    /**
     * The [address attribute][Addressed.address] which the router routes by (e.g. "target", "service", "session")
     */
    var routeBy : String

    /**
     * If no attachment can be found for a message, it will be sent to this default location
     */
    var defaultGateway : Pipe<T>?

    /**
     * Whether the return route should be cached in the message; allows messages to return to a sender
     *
     * @see uuid
     */
    var embedReturns : Boolean

    /**
     * A unique identifier for the router instance; used as the [address attribute][Addressed.address] for
     * [cached return routes][embedReturns]
     */
    val uuid : UUID

    /**
     * Subscribes to the given [pipe][AsyncPipe]; relays any provided messages to their specified targets, delivers any
     * messages received from other attachments addressed with the provided address
     */
    fun attach(address : String, pipe : AsyncPipe<T>)

    /**
     * If a received message's target is not attached directly to this router, the implementation will check for any
     * static routes with the same name (e.g. an [attachment][attach] pointing to another router which has the
     * requested target)
     */
    fun staticRoute(target : String, address : String)

    companion object
    {
        @JvmStatic
        fun <T : Addressed> create(routeBy : String = "target") = RouterImpl<T>(routeBy)
    }
}