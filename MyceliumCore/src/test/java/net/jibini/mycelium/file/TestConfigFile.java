package net.jibini.mycelium.file;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;

import net.jibini.mycelium.config.ConfigFile;

public class TestConfigFile
{
	@Test
	public void testDefaults()
	{
		ConfigFile config = new ConfigFile()
				.defaultValue("name", "Hello, world!");
		assertEquals("Hello, world!", config.valueString("name"));
		
		config.value("name", "Hello, world!")
				.defaultValue("name", "Foo Bar");
		assertEquals("Hello, world!", config.valueString("name"));
	}

	@Test
	public void testSaveLoad() throws FileNotFoundException, IOException
	{
		new ConfigFile()
				.at("test.json")
				.load()
				.defaultValue("name", "Hello, world!")
				.write()
				.deleteOnExit()
				.close();
		
		ConfigFile config = new ConfigFile()
				.at("test.json")
				.load()
				.close();
		
		assertEquals("Hello, world!", config.valueString("name"));
	}

	@Test
	public void testMapNodeStack() throws FileNotFoundException, IOException
	{
		new ConfigFile()
				.at("test.json")
				.load()
					.pushNode("node")
						.pushNode("subnode")
						.defaultValue("name", "Foo Bar")
						.popNode()
					.defaultValue("name", "Hello, world!")
					.popNode()
				.popNode()
				.write()
				.deleteOnExit()
				.close();
		
		ConfigFile config = new ConfigFile()
				.at("test.json")
				.load()
				.close();
		
		assertEquals("Hello, world!", config.pushNode("node").valueString("name"));
		assertEquals("Foo Bar", config.pushNode("node").pushNode("subnode").valueString("name"));
	}
	
	@Test
	public void testArrayNodeStack() throws FileNotFoundException, IOException
	{
		new ConfigFile()
				.at("test.json")
				.load()
					.pushArray("node")
						.putNode()
							.defaultValue("name", "Foo Bar")
						.popNode()
						.put("Hello, world!")
					.popNode()
				.popNode()
				.write()
				.deleteOnExit()
				.close();
		
		ConfigFile config = new ConfigFile()
				.at("test.json")
				.load()
				.close();
		
		assertEquals("Hello, world!", config.pushArray("node").valueString(1));
		assertEquals("Foo Bar", config.pushArray("node").pushNode(0).valueString("name"));
	}
	
	@Test
	public void testDefaultArray()
	{
		ConfigFile config = new ConfigFile()
				.defaultArray("array")
				.put("Hello, world!")
				.popNode()
			.popNode();
		
		assertEquals("Hello, world!", config.pushArray("array").valueString(0));
		
		config = new ConfigFile()
				.pushArray("array")
					.put("Hello, world!")
				.popNode()
				.defaultArray("array")
					.put("Foo Bar")
				.popNode()
			.popNode();
		
		assertEquals("Hello, world!", config.pushArray("array").valueString(0));
	}
	
	@Test
	public void testMixedNodeStack() throws FileNotFoundException, IOException
	{
		new ConfigFile()
				.at("test.json")
				.load()
				.value("datapoints", 24)
				.value("floating-point", 0.421)
					.pushArray("datasample")
						.putNode()
							.value("meta-taken", true) 
							.value("floating-equiv", 0.3f)
						.popNode()
						.putArray()
							.put(0.31)
							.put(0.4123) 
							.putNode()
								.pushNode("node") 
									.defaultValue("default", "success")
								.popNode()
							.popNode()
						.popNode()
					.popNode()
				.popNode()
				.write()
				.deleteOnExit()
				.close();
		
		ConfigFile config = new ConfigFile()
				.at("test.json")
				.load()
				.close();
		
		assertEquals(24, config.valueInt("datapoints"));
		assertEquals(0.421, config.valueDouble("floating-point"), 0.001);
		assertEquals(true, config.pushArray("datasample")
				.pushNode(0)
				.valueBoolean("meta-taken"));
		assertEquals(0.3f, config.pushArray("datasample")
				.pushNode(0)
				.valueFloat("floating-equiv"), 0.01f);
		assertEquals(0.31, config.pushArray("datasample")
				.pushArray(1)
				.valueDouble(0), 0.001);
		assertEquals(0.4123, config.pushArray("datasample")
				.pushArray(1)
				.valueDouble(1), 0.001);
		
		assertEquals("success", config.pushArray("datasample")
				.pushArray(1)
				.pushNode(2)
				.pushNode("node")
				.value("default"));

		assertEquals("{\"datapoints\":24,\"floating-point\":0.421,\"datasample\":["
				+ "{\"floating-equiv\":0.3,\"meta-taken\":true},[0.31,0.4123,{\"no"
				+ "de\":{\"default\":\"success\"}}]]}", config.toString());
	}
	
	@Test
	public void testMapDataTypes()
	{
		ConfigFile config = new ConfigFile()
				.value("object", "value")
				.value("string", "value")
				.value("boolean", true)
				.value("int", 10)
				.value("float", 3.2f)
				.value("double", 43.4d);
		assertEquals(config.value("object"), "value");
		assertEquals(config.valueString("string"), "value");
		assertEquals(config.valueBoolean("boolean"), true);
		assertEquals(config.valueInt("int"), 10);
		assertEquals(config.valueFloat("float"), 3.2f, 0.01f);
		assertEquals(config.valueDouble("double"), 43.4d, 0.001d);
	}
	
	@Test
	public void testArrayDataTypes()
	{
		ConfigFile config = new ConfigFile()
				.pushArray("array")
					.put("value")
					.put("value")
					.put(true)
					.put(10)
					.put(3.2f)
					.put(43.4d)
					.popNode()
				.popNode();
		assertEquals(config.pushArray("array").value(0), "value");
		assertEquals(config.pushArray("array").valueString(1), "value");
		assertEquals(config.pushArray("array").valueBoolean(2), true);
		assertEquals(config.pushArray("array").valueInt(3), 10);
		assertEquals(config.pushArray("array").valueFloat(4), 3.2f, 0.01f);
		assertEquals(config.pushArray("array").valueDouble(5), 43.4d, 0.001d);
	}
	
	@Test
	public void testConfigDelete() throws FileNotFoundException, IOException
	{
		File file = new File("test.json");
		new ConfigFile().from(file).createIfNotExist().close();
		assertEquals(true, file.exists());
		new ConfigFile().from(file).delete();
		assertEquals(false, file.exists());
	}
	
	@Test
	public void testValueJSONArray()
	{
		ConfigFile config = new ConfigFile().pushArray("array").popNode().popNode();
		JSONArray object = config.valueJSONArray("array");
		object.put("Hello, world!");
		assertEquals("Hello, world!", config.pushArray("array").valueString(0));
	}
	
	@Test
	public void testValueJSONObject()
	{
		ConfigFile config = new ConfigFile().pushNode("node").popNode().popNode();
		JSONObject object = config.valueJSONObject("node");
		object.put("message", "Hello, world!");
		assertEquals("Hello, world!", config.pushNode("node").valueString("message"));
	}
	
	@After
	public void deleteTestFile()
	{
		try
		{
			new TextFile()
					.at("test.json")
					.delete();
		} catch (Throwable t)
		{  }
	}
}
