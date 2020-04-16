package net.jibini.mycelium.conf;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
}
