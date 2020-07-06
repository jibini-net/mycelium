package net.jibini.mycelium.transfer.impl

import net.jibini.mycelium.transfer.AsyncPipe
import net.jibini.mycelium.transfer.Pipe
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class AsyncPipeImpl<T>(origin : Pipe<T>) : Pipe<T> by origin, AsyncPipe<T>
{
    private val subscribed = CopyOnWriteArrayList<(T) -> Unit>()

    private var firstSubscription = CountDownLatch(1)

    init
    {
        thread {
            firstSubscription.await()

            while (true)
            {
                val value = this.pull()

                for (callback in subscribed)
                    callback.invoke(value)
            }
        }
    }

    override fun subscribe(callback: (T) -> Unit)
    {
        subscribed.add(callback)
        if (firstSubscription.count > 0)
            firstSubscription.countDown()
    }
}