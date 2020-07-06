package net.jibini.mycelium.transfer.impl

import net.jibini.mycelium.transfer.Addressed
import net.jibini.mycelium.transfer.AsyncPipe
import net.jibini.mycelium.transfer.Pipe
import net.jibini.mycelium.transfer.Router
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class RouterImpl<T : Addressed>(override var routeBy : String) : Router<T>
{
    override var defaultGateway : Pipe<T>? = null
    override var embedReturns : Boolean = false

    override val uuid : UUID = UUID.randomUUID()

    private val attached = ConcurrentHashMap<String, AsyncPipe<T>>()
    private val staticRoutes = ConcurrentHashMap<String, String>()

    override fun attach(address: String, pipe: AsyncPipe<T>)
    {
        attached[address] = pipe

        pipe.subscribe {
            val target = it.findAddress(routeBy)
            val existing = it.findAddress(uuid.toString())

            if (embedReturns) it.address(uuid.toString(), address)

            when
            {
                existing != null -> attached[existing]?.push(it)

                attached.containsKey(target) -> attached[target]?.push(it)
                staticRoutes.containsKey(target) -> attached[staticRoutes[target]]?.push(it)

                else -> defaultGateway?.push(it)
            }
        }
    }

    override fun staticRoute(target : String, address : String)
    {
        staticRoutes[target] = address
    }
}