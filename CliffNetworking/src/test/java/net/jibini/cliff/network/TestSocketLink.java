package net.jibini.cliff.network;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.cliff.routing.Request;

public class TestSocketLink
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private int read = 0;
	
	private Object lock = new Object();
	
	private void w() throws InterruptedException
	{
		if (lock != null)
			synchronized (lock)
			{
				if (lock != null)
					lock.wait();
			}
	}
	
	private void n()
	{
		synchronized (lock)
		{
			lock.notifyAll();
			lock = null;
		}
	}
	
	@Test
	public void testSocketLink() throws IOException, InterruptedException
	{
		ServerSocket server = new ServerSocket();
		server.bind(new InetSocketAddress("0.0.0.0", 25605));
		
		new Thread(() ->
		{
			try
			{
				Socket conn = server.accept();
				SocketLink outgoing = SocketLink.create(conn);
				outgoing.sendRequest(Request.create("Hello", "World", new JSONObject()));
			} catch (IOException ex)
			{
				log.error("Failed to open test server", ex);
			}
		}).start();

		Socket socket = new Socket("localhost", 25605);
		SocketLink inbound = SocketLink.create(socket);
		
		inbound.readRequest((s, r) ->
		{
			log.debug(r.toString());
			read = 1;
			n();
		});
		
		w();
		assertEquals("Message was not received", 1, read);
		
		inbound.close();
		server.close();
	}
}
