package net.jibini.mycelium.json;

import static org.junit.Assert.assertEquals;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import net.jibini.mycelium.map.KeyValuePair;

public class TestJSONArrayBindings
{
	@Test
	public void testDataTypes()
	{
		JSONObject obj = new JSONObject();
		JSONArray arr = new JSONArray();
		
		JSONArrayBindings array = new JSONArrayBindings()
				.insert(0, "Object")
				.insert(1, "String")
				.append(true)
				.append(2)
				.append(0.2f)
				.append(0.002)
				.append(obj)
				.append(arr);
		assertEquals(array.value(0), "Object");
		assertEquals(array.valueString(1), "String");
		assertEquals(array.valueBoolean(2), true);
		assertEquals(array.valueInt(3), 2);
		assertEquals(array.valueFloat(4), 0.2f, 0.0f);
		assertEquals(array.valueDouble(5), 0.002, 0.0);
		assertEquals(array.valueJSONObject(6), obj);
		assertEquals(array.valueJSONArray(7), arr);
	}
	
	@Test
	public void testKeyValuePair()
	{
		JSONArrayBindings array = new JSONArrayBindings()
				.insert(0, "Object")
				.append("String");
		KeyValuePair<Integer, Object> object = array.keyValue(1);
		assertEquals(Integer.valueOf(1), object.key());
		assertEquals("String", object.value());
	}
	
	public void testAppend()
	{
		new JSONArrayBindings()
				.append("Object");
	}
	
	@Test
	public void testHasKey()
	{
		JSONArrayBindings array = new JSONArrayBindings()
				.insert(0, "Object")
				.append("String");
		assertEquals(true, array.hasKey(1));
		assertEquals(false, array.hasKey(2));
	}
	
	@Test
	public void testHasValue()
	{
		JSONArrayBindings array = new JSONArrayBindings()
				.insert(0, "Object")
				.append("String");
		assertEquals(true, array.hasValue("String"));
		assertEquals(false, array.hasValue("Bar"));
	}
	
	/*
	 * Expected behavior:
	 * JSONArrayBindings mimics the insertion behavior of JSONArray,
	 * therefore "inserting" an object at an existing index replaces
	 * the previous value.
	 */
	@Test
	public void testSizing()
	{
		JSONArrayBindings array = new JSONArrayBindings()
				.append("Object")
				.insert(0, "String");
		assertEquals(1, array.size());
		
		array.append("Value");
		assertEquals(2, array.size());
	}
	
	@Test
	public void testKeyValueIterable()
	{
		int c = 0;
		JSONArrayBindings array = new JSONArrayBindings()
				.append(c ++)
				.append(c ++)
				.append(c ++)
				.append(c ++)
				.append(c ++);
		c = 0;
		
		for (KeyValuePair<Integer, Object> keyValue : array.iterable())
		{
			assertEquals(Integer.valueOf(c), keyValue.key());
			assertEquals(c ++, keyValue.value());
		}
	}
	
	@Test
	public void testValueIterable()
	{
		int c = 0;
		JSONArrayBindings array = new JSONArrayBindings()
				.append(c ++)
				.append(c ++)
				.append(c ++)
				.append(c ++)
				.append(c ++);
		c = 0;
		for (Object value : array.values())
			assertEquals(c ++, value);
	}
	
	@Test
	public void testToString()
	{
		JSONArrayBindings array = new JSONArrayBindings()
				.append("Value")
				.append("Hello, world!")
				.append("Foo")
				.append("Bar");
		assertEquals("[\"Value\",\"Hello, world!\",\"Foo\",\"Bar\"]", array.toString());
		assertEquals("[\n" + 
				"    \"Value\",\n" + 
				"    \"Hello, world!\",\n" + 
				"    \"Foo\",\n" + 
				"    \"Bar\"\n" + 
				"]", array.toString(4));
	}
}
