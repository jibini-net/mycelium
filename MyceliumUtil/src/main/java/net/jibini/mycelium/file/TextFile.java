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

import net.jibini.mycelium.link.Closeable;
import net.jibini.mycelium.resource.Checked;

public class TextFile implements Closeable
{
	private Checked<InputStream> in = new Checked<InputStream>()
			.withName("Input Stream");
	private Checked<OutputStream> out = new Checked<OutputStream>()
			.withName("Output Stream");
	private Checked<File> file = new Checked<File>()
			.withName("File");
	
	private boolean open = false;
	
	public TextFile from(InputStream in, OutputStream out) { return from(in).from(out); }
	
	
	public TextFile from(InputStream in)
	{
		this.open = true;
		this.in.value(in);
		return this;
	}
	
	public TextFile from(OutputStream out)
	{
		this.open = true;
		this.out.value(out);
		return this;
	}
	
	
	public TextFile from(File file)
	{
		this.file.value(file);
		return this;
	}
	
	public TextFile at(String path) { return from(new File(path)); }
	
	
	public TextFile createIfNotExist(String write) throws IOException
	{
		if (file.has())
			if (!file.value().exists())
			{
				try
				{
					file.value().getParentFile().mkdirs();
				} catch (Throwable t)
				{  }
				
				file.value().createNewFile();
				this.append(write);
			}
		return this;
	}
	
	public TextFile createIfNotExist() throws IOException { return createIfNotExist(""); }
	
	
	public BufferedReader createReader() throws FileNotFoundException
	{
		openIfNot();
		return new BufferedReader(new InputStreamReader(in.value()));
	}
	
	public BufferedWriter createWriter() throws FileNotFoundException
	{
		openIfNot();
		return new BufferedWriter(new OutputStreamWriter(out.value()));
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
		if (file.has())
			close().openIfNot(false);
		return append(contents);
	}
	
	public TextFile openIfNot(boolean append) throws FileNotFoundException
	{
			if (!open)
			{
				FileInputStream input = new FileInputStream(file.value());
				FileOutputStream output = new FileOutputStream(file.value(), append);
				from(input, output);
			}
			
			return this;
	}
	
	public synchronized TextFile openIfNot() throws FileNotFoundException { return openIfNot(true); }
	
	
	public TextFile close()
	{
		open = false;
		if (in.has())
			try
			{
				in.value().close();
			} catch (IOException ex)
			{  }
		if (out.has())
			try
			{
				out.value().close();
			} catch (IOException ex)
			{  }
		return this;
	}
	
	public TextFile delete()
	{
		file.value().delete();
		return this;
	}
	
	public TextFile deleteOnExit()
	{
		file.value().deleteOnExit();
		return this;
	}

	@Override
	public boolean isAlive() { return open; }
}
