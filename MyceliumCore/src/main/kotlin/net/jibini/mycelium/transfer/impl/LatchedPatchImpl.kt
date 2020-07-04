package net.jibini.mycelium.transfer.impl

import net.jibini.mycelium.transfer.Patch
import net.jibini.mycelium.transfer.Pipe

class LatchedPatchImpl<T> : Patch<T>
{
    private val internal = Pipe.create<T>()

    override fun uplink() = SourcedPipeImpl(this::pull, internal::push)

    override fun push(value: T)
    {
        uplink().push(value)
    }

    override fun pull(): T = internal.pull()
}