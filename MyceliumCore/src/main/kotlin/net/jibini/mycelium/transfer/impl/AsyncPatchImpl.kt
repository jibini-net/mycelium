package net.jibini.mycelium.transfer.impl

import net.jibini.mycelium.transfer.AsyncPatch
import net.jibini.mycelium.transfer.AsyncPipe
import net.jibini.mycelium.transfer.Patch
import net.jibini.mycelium.transfer.Pipe

class AsyncPatchImpl<T>(private val origin : Patch<T>) : Patch<T> by origin, AsyncPatch<T>
{
    private val internal = Pipe.createAsync(this)

    override val uplink : AsyncPipe<T> = Pipe.createAsync(origin.uplink)

    override fun subscribe(callback : (T) -> Unit)
    {
        internal.subscribe(callback)
    }
}