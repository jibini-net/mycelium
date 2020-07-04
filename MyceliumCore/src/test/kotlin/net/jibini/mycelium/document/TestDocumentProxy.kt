package net.jibini.mycelium.document

import net.jibini.mycelium.document.impl.DocumentProxy
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class TestDocumentProxy
{
    @Test fun documentProxyLoadValues()
    {
        val document = DocumentProxy.createFromJSON<TestDocument>(JSONObject()
                .put("values", JSONArray()
                        .put("Hello, world!")
                        .put("Foo, bar")))

        assertEquals("Hello, world!", document.values()[0])
        assertEquals("Foo, bar", document.values()[1])
    }

    @Test fun documentProxyLoadValue()
    {
        val document = DocumentProxy.createFromJSON<TestDocument>(JSONObject()
                .put("value", "Hello, world!"))

        assertEquals("Hello, world!", document.value())
    }

    @Test fun documentProxyLoadSubValues()
    {
        val document = DocumentProxy.createFromJSON<TestDocument>(JSONObject()
                .put("subDocuments", JSONArray()
                        .put(JSONObject().put("value", "Hello, world!"))
                        .put(JSONObject().put("value", "Foo, bar"))))

        assertEquals("Hello, world!", document.subDocuments()[0].value())
        assertEquals("Foo, bar", document.subDocuments()[1].value())
    }

    @Test fun documentProxyLoadSubValue()
    {
        val document = DocumentProxy.createFromJSON<TestDocument>(JSONObject()
                .put("subDocument", JSONObject().put("value", "Hello, world!")))

        assertEquals("Hello, world!", document.subDocument().value())
    }

    @Test fun documentProxyLoadListOfItems()
    {
        val documents = DocumentProxy.createFromJSON<TestDocument>(JSONArray()
                .put(JSONObject().put("value", "Hello, world!"))
                .put(JSONObject().put("value", "Foo, bar")))

        assertEquals("Hello, world!", documents[0].value())
        assertEquals("Foo, bar", documents[1].value())
    }

    @Test fun documentProxyLoadListOfLists()
    {
        val document = DocumentProxy.createFromJSON<TestDocument>(JSONObject()
                .put("listOfLists", JSONArray()
                        .put(JSONArray()
                                .put(JSONObject().put("value", "Hello, world!")))
                        .put(JSONArray()
                                .put(JSONObject().put("value", "Foo"))
                                .put(JSONObject().put("value", "Bar")))))

        assertEquals("Hello, world!", document.listOfLists()[0][0].value())

        assertEquals("Foo", document.listOfLists()[1][0].value())
        assertEquals("Bar", document.listOfLists()[1][1].value())
    }

    @Test fun documentProxyMutateValue()
    {
        val document = DocumentProxy.createFromJSON<TestDocument>(JSONObject()
                .put("value", "Hello, world!"))
        document.value("Foo, bar")

        assertEquals("Foo, bar", document.value())
    }

    @Test fun documentProxyMutateSubValue()
    {
        val document = DocumentProxy.createFromJSON<TestDocument>(JSONObject()
                .put("subDocument", JSONObject().put("value", "Hello, world!")))
        document.subDocument().value("Foo, bar")

        assertEquals("Foo, bar", document.subDocument().value())
    }
}

interface TestDocument
{
    fun values() : List<String>

    fun value() : String

    fun value(value : String)

    fun subDocuments() : List<TestSubDocument>

    fun subDocument() : TestSubDocument

    fun listOfLists() : List<List<TestSubDocument>>
}

interface TestSubDocument
{
    fun value() : String

    fun value(value : String)
}