package net.jibini.mycelium.conf;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

public class TestConfigFile
{
	@Test
	public void testConfigNodes()
	{
		ConfigFile config = new ConfigFile()
				.from(new JSONObject())
				.defaultValue("key", "Hello, world!")
				.value("map", new JSONObject()
						.put("Foo", new JSONObject()
								.put("value", "Bar")
							)
					)
				.value("array", new JSONArray()
						.put("Hello, world!")
						.put("Foo Bar")
					);

		assertEquals("Hello, world!", config.value("key"));
		assertEquals("Bar", config.map("map").map("Foo").<String>value("value"));
		assertEquals("Hello, world!", config.array("array").<String>value(0));
		assertEquals("Foo Bar", config.array("array").<String>value(1));
	}
	
	@Test
	public void testStringGeneration()
	{
		ConfigFile config = new ConfigFile()
				.from(new JSONObject())
				.defaultValue("key", "Hello, world!")
				.value("map", new JSONObject()
						.put("Foo", new JSONObject()
								.put("value", "Bar")
							)
					)
				.value("array", new JSONArray()
						.put("Hello, world!")
						.put("Foo Bar")
					);
		
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
	public void testReadWrite() throws IOException
	{
		new ConfigFile()
				.at("config/test.json")
				.load()
				.deleteOnExit()
				
				.defaultValue("key", "Hello, world!")
				.value("map", new JSONObject()
						.put("Foo", new JSONObject()
								.put("value", "Bar")
							)
					)
				.value("array", new JSONArray()
						.put("Hello, world!")
						.put("Foo Bar")
					)
				
				.write()
				.close();
		
		ConfigFile loaded = new ConfigFile()
				.at("config/test.json")
				.load()
				.close();
		
		assertEquals("Hello, world!", loaded.value("key"));
		assertEquals("Bar", loaded.map("map").map("Foo").<String>value("value"));
		assertEquals("Hello, world!", loaded.array("array").<String>value(0));
		assertEquals("Foo Bar", loaded.array("array").<String>value(1));
	}
	
	@Test
	public void testVariousSources() throws IOException
	{
		File file = new File("config/test.json");
		file.deleteOnExit();
		
		new ConfigFile()
				.from(file)
				.load()
				.close();
		new ConfigFile()
				.to(new FileOutputStream(file))
				.from(new JSONObject())
				.defaultValue("value", "Hello, world!")
				.write()
				.close();
		assertEquals("Hello, world!", new ConfigFile()
				.from(new FileInputStream(file))
				.load()
				.close()
				.<String>value("value"));
		assertEquals("Hello, world!", new ConfigFile()
				.from(new FileInputStream(file), new FileOutputStream(file, true))
				.load()
				.close()
				.<String>value("value"));
		assertEquals("Hello, world!", new ConfigFile()
				.from(file)
				.load()
				.close()
				.delete()
				.<String>value("value"));
	}
}
