package net.jibini.mycelium.conf;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.jibini.mycelium.json.JSONObjectBindings;

public class TestConfigFile
{
	@Test
	public void testConfigNodes()
	{
		int c = 0;
		ConfigFile config = new ConfigFile()
				.defaultValue("key", "Hello, world!")
				.pushMap("map")
					.pushMap("Foo")
						.defaultValue("value", "Bar")
					.pop()
				.pop()
				.pushArray("array")
					.value(c ++, "Hello, world!")
					.value(c ++, "Foo Bar")
				.pop();
		assertEquals("Hello, world!", config.value("key"));
		assertEquals("Bar", config.pushMap("map").pushMap("Foo").value("value"));
		assertEquals("Hello, world!", config.pushArray("array").value(0));
		assertEquals("Foo Bar", config.pushArray("array").value(1));
	}
	
	@Test
	public void testOverPopped()
	{
		int c = 0;
		ConfigFile config = new ConfigFile()
				.defaultValue("key", "Hello, world!")
				.pushMap("map")
					.pushMap("Foo")
						.defaultValue("value", "Bar")
					.pop()
				.pop()
				.pushArray("array")
					.value(c ++, "Hello, world!")
					.value(c ++, "Foo Bar")
				.pop()
				.pop()
				.pop();
		assertEquals("Hello, world!", config.value("key"));
		assertEquals("Bar", config.pushMap("map").pushMap("Foo").value("value"));
		assertEquals("Hello, world!", config.pushArray("array").value(0));
		assertEquals("Foo Bar", config.pushArray("array").value(1));
	}
	
	@Test
	public void testNodePopped()
	{
		SpawnedConfigNode<String, ConfigFile> node = new SpawnedConfigNode<String, ConfigFile>()
				.withDataMap(new JSONObjectBindings());
		node.pushMap("map")
		.pop();
	}
	
	@Test(expected=RuntimeException.class)
	public void testOrphanPopped()
	{
		SpawnedConfigNode<String, ConfigFile> node = new SpawnedConfigNode<String, ConfigFile>()
				.withDataMap(new JSONObjectBindings());
		node.pushMap("map")
		.pop()
		.pop();
	}
	
	@Test(expected=RuntimeException.class)
	public void testNoDataMapGiven()
	{
		SpawnedConfigNode<String, ConfigFile> node = new SpawnedConfigNode<String, ConfigFile>();
		node.pushMap("map");
	}
	
	@Test
	public void testStringGeneration()
	{
		int c = 0;
		ConfigFile config = new ConfigFile()
				.defaultValue("key", "Hello, world!")
				.pushMap("map")
					.pushMap("Foo")
						.defaultValue("value", "Bar")
					.pop()
				.pop()
				.pushArray("array")
					.value(c ++, "Hello, world!")
					.value(c ++, "Foo Bar")
				.pop();
		assertEquals("{\n" + 
				"    \"array\": [\n" + 
				"        \"Hello, world!\",\n" + 
				"        \"Foo Bar\"\n" + 
				"    ],\n" + 
				"    \"map\": {\"Foo\": {\"value\": \"Bar\"}},\n" + 
				"    \"key\": \"Hello, world!\"\n" + 
				"}", config.toString(4));
		assertEquals("{\"array\":[\"Hello, world!\",\"Foo Bar\"],\"map\":{\"Foo\":{\"value\""
				+ ":\"Bar\"}},\"key\":\"Hello, world!\"}", config.toString());
	}
	
	@Test
	public void testConfigMapTypes()
	{
		ConfigFile config = new ConfigFile()
				.defaultValue("object", "Object")
				.defaultValue("string", "String")
				.defaultValue("boolean", true)
				.defaultValue("int", 2)
				.defaultValue("float", 0.2f)
				.defaultValue("double", 0.002);
		assertEquals(config.value("object"), "Object");
		assertEquals(config.valueString("string"), "String");
		assertEquals(config.valueBoolean("boolean"), true);
		assertEquals(config.valueInt("int"), 2);
		assertEquals(config.valueFloat("float"), 0.2f, 0.0f);
		assertEquals(config.valueDouble("double"), 0.002, 0.0);
	}
	
	@Test
	public void testConfigArrayTypes()
	{
		ConfigFile config = new ConfigFile()
				.pushArray("array")
					.append("Object")
					.append("String")
					.append(true)
					.append(2)
					.append(0.2f)
					.append(0.002)
				.pop();
		assertEquals(config.pushArray("array").value(0), "Object");
		assertEquals(config.pushArray("array").valueString(1), "String");
		assertEquals(config.pushArray("array").valueBoolean(2), true);
		assertEquals(config.pushArray("array").valueInt(3), 2);
		assertEquals(config.pushArray("array").valueFloat(4), 0.2f, 0.0f);
		assertEquals(config.pushArray("array").valueDouble(5), 0.002, 0.0);
	}
}
