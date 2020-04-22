package net.jibini.mycelium.file;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import net.jibini.mycelium.error.LoggingException;

public class TextFileAppender extends AppenderSkeleton
{
	private TextFile file = new TextFile()
			.at("log/latest.log");
	
	public TextFileAppender()
	{
		try
		{
			File current = new File("log/latest.log");
			
			if (current.exists())
			{
				Date date = new Date(current.lastModified());
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
				String dateText = dateFormat.format(date);
				current.renameTo(new File("log/" + dateText + ".log"));
			}
			
			file.createIfNotExist();
		} catch (IOException ex)
		{
			throw new LoggingException("Failed to start text-file log", ex);
		}
	}

	@Override
	public void close() { file.close(); }

	@Override
	public boolean requiresLayout() { return true; }
	

	@Override
	protected void append(LoggingEvent event)
	{
		try
		{
			file.append(getLayout().format(event));
		} catch (IOException ex)
		{
			throw new LoggingException("Failed to write to text-file log", ex);
		}
	}
}
