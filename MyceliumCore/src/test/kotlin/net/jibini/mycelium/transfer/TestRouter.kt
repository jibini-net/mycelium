package net.jibini.mycelium.transfer

import net.jibini.mycelium.transfer.impl.AddressedImpl
import org.junit.Assert.assertEquals
import org.junit.Test

class TestRouter
{
    class AddressedString(var value : String) : Addressed by AddressedImpl()

    @Test fun routerSendAToBEmbedReturns()
    {
        val router = Router.create<AddressedString>()
        router.embedReturns = true

        val a = Pipe.createAsync<AddressedString>()
        router.attach("a", a)
        val b = Pipe.createAsync<AddressedString>()
        router.attach("b", b)

        val addressedString = AddressedString("Hello, world!")
        addressedString.address("target", "b")
        a.push(addressedString)

        val pulled = b.pull()
        assertEquals("Hello, world!", pulled.value)
        assertEquals("a", pulled.findAddress(router.uuid.toString()))
    }

    @Test fun routerSendAToBStaticRoute()
    {
        val router = Router.create<AddressedString>()
        router.embedReturns = true

        val a = Pipe.createAsync<AddressedString>()
        router.attach("a", a)
        val b = Pipe.createAsync<AddressedString>()
        router.attach("b", b)
        router.staticRoute("c", "b")

        val addressedString = AddressedString("Hello, world!")
        addressedString.address("target", "c")
        a.push(addressedString)

        val pulled = b.pull()
        assertEquals("Hello, world!", pulled.value)
        assertEquals("a", pulled.findAddress(router.uuid.toString()))
    }

    @Test fun routerSendAToBDefaultGateway()
    {
        val router = Router.create<AddressedString>()
        router.embedReturns = true

        val a = Pipe.createAsync<AddressedString>()
        router.attach("a", a)
        val b = Pipe.createAsync<AddressedString>()
        router.defaultGateway = b

        val addressedString = AddressedString("Hello, world!")
        addressedString.address("target", "b")
        a.push(addressedString)

        val pulled = b.pull()
        assertEquals("Hello, world!", pulled.value)
        assertEquals("a", pulled.findAddress(router.uuid.toString()))
    }
}