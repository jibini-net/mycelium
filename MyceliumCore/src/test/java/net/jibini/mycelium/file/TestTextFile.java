package net.jibini.mycelium.file;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.Test;

import net.jibini.mycelium.file.TextFile;

public class TestTextFile
{
	@Test
	public void testReadWrite() throws IOException
	{
		String test = new TextFile()
				.at("test.txt")
				.createIfNotExist()
				.deleteOnExit()
				.overwrite("Hello, world!")
				.readRemaining(true);
		assertEquals("Hello, world!", test);
	}
	
	@Test
	public void testNewline() throws IOException
	{
		String test = new TextFile()
				.at("test.txt")
				.createIfNotExist()
				.deleteOnExit()
				.overwrite("Hello,\n world!\n")
				.readRemaining(true);
		assertEquals("Hello,\n world!\n", test);
	}
	
	@Test
	public void testNewlines() throws IOException
	{
		String test = new TextFile()
				.at("test.txt")
				.createIfNotExist()
				.deleteOnExit()
				.overwrite("Hello,\n world!\n\n")
				.readRemaining(true);
		assertEquals("Hello,\n world!\n\n", test);
	}
	
	@Test
	public void testSpecialChars() throws IOException
	{
		String test = new TextFile()
				.at("test.txt")
				.createIfNotExist()
				.deleteOnExit()
				.overwrite("Hello,\t world!\t\n\t")
				.readRemaining(true);
		assertEquals("Hello,\t world!\t\n\t", test);
	}
	
	@Test
	public void testAppendMode() throws IOException
	{
		String test = new TextFile()
				.at("test.txt")
				.createIfNotExist()
				.deleteOnExit()
				.append("Hello, ")
				.append("world!")
				.readRemaining(true);
		assertEquals("Hello, world!", test);
	}
	
	@Test
	public void testNonAppendMode() throws IOException
	{
		String test = new TextFile()
				.at("test.txt")
				.createIfNotExist()
				.deleteOnExit()
				.append("Foo")
				.append("Bar")
				.close().openIfNot(false)
				.append("Hello, world!")
				.readRemaining(true);
		assertEquals("Hello, world!", test);
	}
	
	@Test
	public void testOverwriteMode() throws IOException
	{
		String test = new TextFile()
				.at("test.txt")
				.createIfNotExist()
				.deleteOnExit()
				.append("Foo")
				.append("Bar")
				.overwrite("Hello, world!")
				.readRemaining(true);
		assertEquals("Hello, world!", test);
	}
	
	@Test
	public void testDoubleReadRemaining() throws IOException
	{
		TextFile file = new TextFile()
				.at("test.txt")
				.createIfNotExist()
				.deleteOnExit()
				.append("Hello, world!");
		
		String test = file.readRemaining();
		assertEquals("Hello, world!", test);
		
		file.append("Foo, Bar");
		test = file.readRemaining(true);
		assertEquals("Foo, Bar", test);
	}
	
	@After
	public void deleteTestFile()
	{
		try
		{
			new TextFile()
					.at("test.txt")
					.delete();
		} catch (Throwable t)
		{  }
	}
}
