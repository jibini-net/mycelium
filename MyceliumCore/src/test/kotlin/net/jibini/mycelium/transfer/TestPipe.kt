package net.jibini.mycelium.transfer

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class TestPipe
{
    @Test fun pipeTransferSynchronous()
    {
        val pipe = Pipe.create<String>()
        pipe.push("Hello, world!")

        assertEquals("Hello, world!", pipe.pull())
    }

    @Test fun pipeTransferAwaitSynchronous()
    {
        val pipe = Pipe.create<String>()

        var valueA : String? = null
        var valueB : String? = null

        val await = thread {
            valueA = pipe.pull()
            valueB = pipe.pull()
        }

        pipe.push("Hello, world!")
        pipe.push("Foo, bar")

        await.join()
        assertEquals("Hello, world!", valueA!!)
        assertEquals("Foo, bar", valueB!!)
    }

    @Test fun pipeTransferSubscription()
    {
        var value : String? = null

        val pipe = Pipe.createAsync<String>()
        val latch = CountDownLatch(1)

        pipe.subscribe {
            value = it
            latch.countDown()
        }

        pipe.push("Hello, world!")

        latch.await()
        assertEquals("Hello, world!", value!!)
    }
}