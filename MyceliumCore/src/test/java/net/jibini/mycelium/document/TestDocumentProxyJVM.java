package net.jibini.mycelium.document;

import net.jibini.mycelium.document.impl.DocumentProxy;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestDocumentProxyJVM
{
    @Test
    public void documentProxyInJVM()
    {
        TestDocument document = DocumentProxy.createFromJSON(TestDocument.class, new JSONObject()
                .put("value", "Hello, world!"));
        assertEquals("Hello, world!", document.value());
    }
}
