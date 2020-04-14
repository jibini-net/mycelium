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

public final class TextFile
{
	private File file = null;
	private InputStream in = null;
	private OutputStream out = null;
	
	private boolean open = false;
	private Object openLock = new Object();
	
	public TextFile from(InputStream in, OutputStream out)
	{
		this.open = true;
		this.in = in;
		this.out = out;
		return this;
	}
	
	public TextFile from(InputStream in) { return from(in, null); }
	
	public TextFile from(OutputStream out) { return from(null, out); }
	
	
	public TextFile from(File file)
	{
		this.file = file;
		return this;
	}
	
	public TextFile at(String path) { return from(new File(path)); }
	
	
	public TextFile createIfNotExist(String write) throws IOException
	{
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
		
		return from(file);
	}
	
	public TextFile createIfNotExist() throws IOException { return createIfNotExist(""); }
	
	
	public BufferedReader createReader() throws FileNotFoundException
	{
		openIfNot();
		return new BufferedReader(new InputStreamReader(in));
	}
	
	public BufferedWriter createWriter() throws FileNotFoundException
	{
		openIfNot();
		return new BufferedWriter(new OutputStreamWriter(out));
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
		close().openIfNot(false);
		return append(contents);
	}
	
	public synchronized TextFile openIfNot(boolean append) throws FileNotFoundException
	{
		synchronized (openLock)
		{
			if (!open)
			{
				in = new FileInputStream(file);
				out = new FileOutputStream(file, append);
			}
			
			open = true;
			return this;
		}
	}
	
	public synchronized TextFile openIfNot() throws FileNotFoundException
	{
		return openIfNot(true);
	}
	
	public synchronized TextFile close() throws IOException
	{
		synchronized (openLock)
		{
			open = false;
			if (in != null)
				in.close();
			if (out != null)
				out.close();
			return this;
		}
	}
	
	public TextFile delete()
	{
		if (file == null)
			throw new RuntimeException("No file specified, cannot delete");
		else
			file.delete();
		return this;
	}
	
	public TextFile deleteOnExit()
	{
		if (file == null)
			throw new RuntimeException("No file specified, cannot delete");
		else
			file.deleteOnExit();
		return this;
	}
}
