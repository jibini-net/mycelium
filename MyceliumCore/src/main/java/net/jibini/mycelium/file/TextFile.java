package net.jibini.mycelium.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import net.jibini.mycelium.error.MissingResourceException;

public final class TextFile
{
	private InputStream in;
	private OutputStream out;
	private File file;
	
	private boolean open = false;
	private boolean hasFile = false;
	private boolean hasIn = false;
	private boolean hasOut = false;
	
	public TextFile from(InputStream in, OutputStream out) { return from(in).from(out); }
	
	
	public TextFile from(InputStream in)
	{
		this.open = true;
		this.in = in;
		this.hasIn = true;
		return this;
	}
	
	public TextFile from(OutputStream out)
	{
		this.open = true;
		this.out = out;
		this.hasOut = true;
		return this;
	}
	
	
	public TextFile from(File file)
	{
		this.file = file;
		this.hasFile = true;
		return this;
	}
	
	public TextFile at(String path) { return from(new File(path)); }
	
	
	public TextFile createIfNotExist(String write) throws IOException
	{
		if (hasFile)
			if (!file.exists())
			{
				try
				{
					file.getParentFile().mkdirs();
				} catch (Throwable t)
				{  }
				
				file.createNewFile();
				this.append(write);
			}
		return this;
	}
	
	public TextFile createIfNotExist() throws IOException { return createIfNotExist(""); }
	
	
	public BufferedReader createReader() throws FileNotFoundException
	{
		openIfNot();
		if (hasIn)
			return new BufferedReader(new InputStreamReader(in));
		else
			throw new MissingResourceException("No input stream defined for text file");
	}
	
	public BufferedWriter createWriter() throws FileNotFoundException
	{
		openIfNot();
		if (hasOut)
			return new BufferedWriter(new OutputStreamWriter(out));
		else
			throw new MissingResourceException("No output stream defined for text file");
	}
	
	public String readRemaining(boolean closeAfter) throws IOException
	{
		BufferedReader reader = createReader();
		StringBuilder builder = new StringBuilder();
		int c;
		while ((c = reader.read()) != -1)
			builder.append((char)c);
		
		if (closeAfter)
			close();
		return builder.toString();
	}
	
	public String readRemaining() throws IOException { return readRemaining(false); }
	
	
	public TextFile append(String contents) throws IOException
	{
		BufferedWriter writer = createWriter();
		writer.write(contents);
		writer.flush();
		return this;
	}

	public TextFile overwrite(String contents) throws IOException
	{
		if (hasFile)
			close().openIfNot(false);
		return append(contents);
	}
	
	public TextFile openIfNot(boolean append) throws FileNotFoundException
	{
			if (!open)
				if (hasFile)
				{
					FileInputStream input = new FileInputStream(file);
					FileOutputStream output = new FileOutputStream(file, append);
					from(input, output);
				}
				else
					throw new MissingResourceException("Cannot open, no file or streams specified");
			return this;
	}
	
	public synchronized TextFile openIfNot() throws FileNotFoundException { return openIfNot(true); }
	
	
	public TextFile close() throws IOException
	{
		open = false;
		if (hasIn)
			in.close();
		if (hasOut)
			out.close();
		return this;
	}
	
	public TextFile delete()
	{
		if (hasFile)
			file.delete();
		else
			throw new MissingResourceException("No file specified, cannot delete");
		return this;
	}
	
	public TextFile deleteOnExit()
	{
		if (hasFile)
			file.deleteOnExit();
		else
			throw new MissingResourceException("No file specified, cannot delete");
		return this;
	}
}
