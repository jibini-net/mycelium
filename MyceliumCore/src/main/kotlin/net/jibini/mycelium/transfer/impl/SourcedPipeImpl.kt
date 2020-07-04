package net.jibini.mycelium.transfer.impl

import net.jibini.mycelium.transfer.Pipe

class SourcedPipeImpl<T>(private val nextItem : () -> T, private val itemPushed : (T) -> Unit) : Pipe<T>
{
    override fun push(value: T)
    {
        itemPushed.invoke(value)
    }

    override fun pull(): T = nextItem.invoke()
}