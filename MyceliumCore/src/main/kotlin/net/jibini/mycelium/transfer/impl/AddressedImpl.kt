package net.jibini.mycelium.transfer.impl

import net.jibini.mycelium.transfer.Addressed
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap

class AddressedImpl : Addressed
{
    private val addresses = ConcurrentHashMap<String, String>()

    override fun address(attr: String, address: String)
    {
        addresses[attr] = address
    }

    override fun findAddress(attr: String) = addresses[attr]
}

class JSONAddressedImpl(private val internal : JSONObject) : Addressed
{
    init
    {
        if (!internal.has("route"))
            internal.put("route", JSONObject())
    }

    override fun address(attr: String, address: String)
    {
        internal.getJSONObject("route").put(attr, address)
    }

    override fun findAddress(attr : String) =  if (internal.getJSONObject("route").has(attr))
        internal.getJSONObject("route").getString(attr)  else null
}