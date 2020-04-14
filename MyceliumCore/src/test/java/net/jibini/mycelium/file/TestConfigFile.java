package net.jibini.mycelium.file;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

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
				.close();
		
		ConfigFile config = new ConfigFile()
				.at("test.json")
				.load()
				.close();
		
		assertEquals("Hello, world!", config.pushArray("node").valueString(1));
		assertEquals("Foo Bar", config.pushArray("node").pushNode(0).valueString("name"));
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
				.valueString("default"));
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
