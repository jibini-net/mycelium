package net.jibini.mycelium.json;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.jibini.mycelium.map.KeyValuePair;

public class TestJSONObjectBindings
{
	@Test
	public void testDataTypes()
	{
		JSONObjectBindings map = new JSONObjectBindings()
				.insert("object", "Object")
				.insert("string", "String")
				.insert("boolean", true)
				.insert("int", 2)
				.insert("float", 0.2f)
				.insert("double", 0.002);
		assertEquals(map.value("object"), "Object");
		assertEquals(map.valueString("string"), "String");
		assertEquals(map.valueBoolean("boolean"), true);
		assertEquals(map.valueInt("int"), 2);
		assertEquals(map.valueFloat("float"), 0.2f, 0.0f);
		assertEquals(map.valueDouble("double"), 0.002, 0.0);
	}
	
	@Test
	public void testKeyValuePair()
	{
		JSONObjectBindings map = new JSONObjectBindings()
				.insert("object", "Object")
				.insert("string", "String");
		KeyValuePair<String, Object> object = map.keyValue("object");
		assertEquals("object", object.key());
		assertEquals("Object", object.value());
	}
	
	@Test(expected=RuntimeException.class)
	public void testAppend()
	{
		new JSONObjectBindings()
				.append("Object");
	}
	
	@Test
	public void testHasKey()
	{
		JSONObjectBindings map = new JSONObjectBindings()
				.insert("object", "Object")
				.insert("string", "String");
		assertEquals(true, map.hasKey("object"));
		assertEquals(false, map.hasKey("Foo"));
	}
	
	@Test
	public void testHasValue()
	{
		JSONObjectBindings map = new JSONObjectBindings()
				.insert("object", "Object")
				.insert("string", "String");
		assertEquals(true, map.hasValue("String"));
		assertEquals(false, map.hasValue("Bar"));
	}
	
	@Test
	public void testSizing()
	{
		JSONObjectBindings map = new JSONObjectBindings()
				.insert("object", "Object")
				.insert("string", "String");
		assertEquals(2, map.size());
		
		map.insert("third", "Value");
		assertEquals(3, map.size());
	}
	
	@Test
	public void testKeyValueIterable()
	{
		int c = 0;
		JSONObjectBindings map = new JSONObjectBindings()
				.insert(String.valueOf(c), c ++)
				.insert(String.valueOf(c), c ++)
				.insert(String.valueOf(c), c ++)
				.insert(String.valueOf(c), c ++)
				.insert(String.valueOf(c), c ++);
		c = 0;
		
		for (KeyValuePair<String, Object> keyValue : map.iterable())
		{
			assertEquals(String.valueOf(c), keyValue.key());
			assertEquals(c ++, keyValue.value());
		}
	}
	
	@Test
	public void testValueIterable()
	{
		int c = 0;
		JSONObjectBindings map = new JSONObjectBindings()
				.insert(String.valueOf(c), c ++)
				.insert(String.valueOf(c), c ++)
				.insert(String.valueOf(c), c ++)
				.insert(String.valueOf(c), c ++)
				.insert(String.valueOf(c), c ++);
		c = 0;
		for (Object value : map.values())
			assertEquals(c ++, value);
	}
}
