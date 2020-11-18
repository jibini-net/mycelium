package net.jibini.mycelium

import net.jibini.mycelium.document.DocumentMessage
import net.jibini.mycelium.impl.MyceliumSporeImpl
import net.jibini.mycelium.transfer.Pipe
import net.jibini.mycelium.transfer.Router
import org.json.JSONObject

interface MyceliumSpore
{
    val requestRouter : Router<DocumentMessage>

    val uplink : Pipe<DocumentMessage>

    fun send(target : String, message : DocumentMessage)

    fun broadcast(message : DocumentMessage)

    fun broadcastAvailability(status: String)
    {
        val availabilityDocument = DocumentMessage(JSONObject())
        availabilityDocument.build<MyceliumSporeAvailability>().availabilityStatus(status)

        this.broadcast(availabilityDocument)
    }

    fun <T> subscribe(interfaceClass : Class<T>, request : String, callback : (T) -> Unit)

    companion object
    {
        @JvmStatic
        fun create(uplink : Pipe<DocumentMessage>) = MyceliumSporeImpl(uplink)

        @JvmStatic val STATUS_ONLINE = "online"

        @JvmStatic val STATUS_OFFLINE = "offline"
    }
}

inline fun <reified T> MyceliumSpore.subscribe(request : String, noinline callback : (T) -> Unit)
{
    subscribe(T::class.java, request, callback)
}

interface MyceliumSporeAvailability
{
    fun availabilityStatus() : String

    fun availabilityStatus(status : String)
}