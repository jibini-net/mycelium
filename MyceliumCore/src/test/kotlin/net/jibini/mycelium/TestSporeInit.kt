package net.jibini.mycelium

import net.jibini.mycelium.document.DocumentMessage
import net.jibini.mycelium.document.TestDocument
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class TestSporeInit
{
    @Test fun sporeEstablishAddressedUplink()
    {
        val spore = Mycelium.createSpore("TestSpore")
        val sender = Mycelium.createSpore("Sender")

        val message = DocumentMessage(JSONObject().put("value", "Hello, world!"))
        message.address("target", "TestSpore")
        sender.uplink.push(message)

        assertEquals("Hello, world!", spore.uplink.pull().build<TestDocument>().value())
    }
}