package net.jibini.mycelium.impl

import net.jibini.mycelium.MyceliumSpore
import net.jibini.mycelium.document.DocumentMessage
import net.jibini.mycelium.transfer.Pipe

class MyceliumSporeImpl(override val uplink: Pipe<DocumentMessage>) : MyceliumSpore