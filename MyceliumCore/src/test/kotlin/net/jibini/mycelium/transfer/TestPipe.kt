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
        var value : String? = null

        val pipe = Pipe.create<String>()

        val await = thread {
            value = pipe.pull()
        }

        pipe.push("Hello, world!")

        await.join()
        assertEquals("Hello, world!", value!!)
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