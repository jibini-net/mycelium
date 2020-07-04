package net.jibini.mycelium.transfer.impl

import net.jibini.mycelium.transfer.AsyncPipe
import net.jibini.mycelium.transfer.Pipe
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread

class LatchedAsyncPipeImpl<T> : Pipe<T> by LatchedPipeImpl<T>(), AsyncPipe<T>
{
    private val subscribed = CopyOnWriteArrayList<(T) -> Unit>()

    init
    {
        thread {
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
    }
}