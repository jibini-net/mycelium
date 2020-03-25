package net.jibini.cliff.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.cliff.routing.AbstractLink;
import net.jibini.cliff.routing.Request;

public class SocketLink extends AbstractLink
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private Socket connection;
	
	private BufferedReader reader;
	private PrintWriter writer;
	
	private Thread readerThread = new Thread(() ->
	{
		while (!connection.isClosed())
		{
			try
			{
				Thread.yield();
				String requestContents = reader.readLine();
				
				if (requestContents == null)
					close();
				else
				{
					Request request = Request.create(requestContents);
					pushRequest(request);
				}
			} catch (SocketException ex)
			{
				log.debug("Socket link connection closed or reset", ex);
				
				close();
			} catch (Throwable t)
			{
				log.error("Socket link failed to read request", t);
			}
		}
	});
	
	private SocketLink()
	{}
	
	public static SocketLink create(Socket connection) throws IOException
	{
		SocketLink result = new SocketLink();
		result.connection = connection;
		
		result.reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		result.writer = new PrintWriter(connection.getOutputStream());
		
		result.readerThread.setName("SocketReader");
		result.readerThread.start();
		result.startThread();
		
		return result;
	}
	
	@Override
	public void sendRequest(Request request)
	{
		writer.println(request.toString());
		if (writer.checkError())
			log.error("Socket link failed to write request");
	}

	@Override
	public void close()
	{
		try
		{
			log.debug("Closing socket link to '" + connection.getInetAddress().toString() + "'");
			connection.close();
		} catch (IOException ex)
		{
			log.error("Failed to close socket link", ex);
		}
		
		super.close();
	}
}
