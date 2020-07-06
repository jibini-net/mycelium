package net.jibini.mycelium

import net.jibini.mycelium.document.DocumentMessage
import net.jibini.mycelium.transfer.Patch
import net.jibini.mycelium.transfer.Router

object Mycelium
{
    @JvmStatic
    val localRouter = Router.create<DocumentMessage>()

    init
    {
        localRouter.embedReturns = true
        localRouter.routeBy = "target"
    }

    @JvmStatic
    fun createSpore(address : String) : MyceliumSpore
    {
        val patch = Patch.createAsync<DocumentMessage>()
        localRouter.attach(address, patch.uplink)

        return MyceliumSpore.create(patch)
    }
}