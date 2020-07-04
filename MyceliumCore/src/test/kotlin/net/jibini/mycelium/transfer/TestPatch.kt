package net.jibini.mycelium.transfer

import org.junit.Assert
import org.junit.Test

class TestPatch
{
    @Test fun patchUpstreamSynchronous()
    {
        val patch = Patch.create<String>()
        patch.push("Hello, world!")

        Assert.assertEquals("Hello, world!", patch.uplink().pull())
    }

    @Test fun patchDownstreamSynchronous()
    {
        val patch = Patch.create<String>()
        patch.uplink().push("Hello, world!")

        Assert.assertEquals("Hello, world!", patch.pull())
    }
}