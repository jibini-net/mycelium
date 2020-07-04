package net.jibini.mycelium.transfer.impl

import net.jibini.mycelium.transfer.Addressed
import java.util.concurrent.ConcurrentHashMap

class AddressedImpl : Addressed
{
    private val addresses = ConcurrentHashMap<String, String>()

    override fun address(attr: String, address: String)
    {
        addresses[attr] = address
    }

    override fun findAddress(attr: String): String? = addresses[attr]
}