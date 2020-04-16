package net.jibini.mycelium.file;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.jibini.mycelium.file.TextFile;

public class TestTextFile
{
	private void writeTestFile(File file) throws IOException
	{
		FileOutputStream output = new FileOutputStream(file);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
		writer.write("Hello, world!");
		writer.close();
	}
	
	private void readAndAssert(File file) throws IOException
	{
		FileInputStream input = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		assertEquals("Hello, world!", reader.readLine());
		reader.close();
	}
	
	@Test
	public void testWrite() throws IOException
	{
		File file = new File("test.txt");
		file.createNewFile();
		file.deleteOnExit();
		
		new TextFile()
			.from(new FileOutputStream(file))
			.append("Hello, world!")
			.close();
		readAndAssert(file);
	}
	
	@Test
	public void testFromFileWrite() throws IOException
	{
		File file = new File("test.txt");
		
		new TextFile()
			.from(file)
			.createIfNotExist("Hello, world!")
			.deleteOnExit()
			.close();
		readAndAssert(file);
	}
	
	@Test
	public void testSubDirectoryFile() throws IOException
	{
		File file = new File("config/test/test.json");
		new TextFile()
			.from(file)
			.createIfNotExist("Hello, world!")
			.close();
		readAndAssert(file);
		
		file.delete();
		file.getParentFile().delete();
	}

	@Test
	public void testRead() throws IOException
	{
		File file = new File("test.txt");
		writeTestFile(file);
		
		assertEquals("Hello, world!", new TextFile()
			.from(new FileInputStream(file))
			.readRemaining(true));
	}
	
	@Test
	public void testReadRemaining() throws IOException
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
	
	@Test(expected=RuntimeException.class)
	public void testNoStreams() throws IOException
	{
		new TextFile()
				.createReader();
	}
	
	@Test(expected=RuntimeException.class)
	public void testNoInputStream() throws IOException
	{
		new TextFile()
				.from(System.out)
				.createReader();
	}
	
	@Test(expected=RuntimeException.class)
	public void testNoOutputStream() throws IOException
	{
		new TextFile()
				.from(System.in)
				.createWriter();
	}
	
	@Test(expected=RuntimeException.class)
	public void testNoFileDelete() throws IOException
	{
		new TextFile().delete();
	}
	
	@Test(expected=RuntimeException.class)
	public void testNoFileDeleteOnExit() throws IOException
	{
		new TextFile().deleteOnExit();
	}

	@Before
	@After
	public void deleteTestFile()
	{
		try
		{
			new TextFile()
					.at("test.txt")
					.delete();
		} catch (Throwable t)
		{ t.printStackTrace(); }
	}
}
