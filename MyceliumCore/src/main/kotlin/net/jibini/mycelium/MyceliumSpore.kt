package net.jibini.mycelium

import net.jibini.mycelium.document.DocumentMessage
import net.jibini.mycelium.impl.MyceliumSporeImpl
import net.jibini.mycelium.transfer.Pipe

interface MyceliumSpore
{
    val uplink : Pipe<DocumentMessage>

    companion object
    {
        @JvmStatic
        fun create(uplink : Pipe<DocumentMessage>) = MyceliumSporeImpl(uplink)
    }
}