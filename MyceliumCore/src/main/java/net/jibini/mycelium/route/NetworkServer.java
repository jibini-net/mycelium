package net.jibini.mycelium.route;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.mycelium.error.NetworkException;
import net.jibini.mycelium.link.Closeable;
import net.jibini.mycelium.thread.NamedThread;

public class NetworkServer implements Closeable
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private ServerSocket serverSocket;
	private List<Socket> opened = new CopyOnWriteArrayList<>();
	private RequestSwitch targets = new RequestSwitch();
	
	private boolean embedInteraction = false;
	
	private NamedThread acceptThread = new NamedThread()
			.withRunnable(() -> 
			{
				while (!serverSocket.isClosed())
				{
					acceptLoop();
					Thread.yield();
				}
			})
//			.asDaemon()
			.withName("ServerAcceptThread");
	
	private void acceptLoop()
	{
		try
		{
			Socket inbound = serverSocket.accept();
			opened.add(inbound);
			log.debug("Accepted inbound connection");
			NetworkAdapter adapter = new NetworkAdapter()
					.withSocket(inbound);
			
			if (embedInteraction)
				adapter.embedInteraction();
			targets.attach(adapter);
			
			// Hard limit; only accept up to 50 times/second
			Thread.sleep(20);
		} catch (Throwable t)
		{
			if (System.getProperties().getOrDefault("verboseNetworking", false).equals("true"))
				log.warn("Unable to accept incoming connection", t);
		}
	}
	
	public NetworkServer withServerSocket(ServerSocket socket)
	{ this.serverSocket = socket; return this; }
	
	public NetworkServer embedInteraction()
	{ this.embedInteraction = true; return this; }
	
	
	public NetworkServer attach(NetworkMember member)
	{ targets.attach(member); return this; }
	
	public NetworkServer withDefaultGateway(NetworkMember gateway)
	{ targets.withDefaultGateway(gateway); return this; }
	
	public NetworkServer start()
	{ acceptThread.start(); return this; }
	
	public RequestSwitch router()
	{ return targets; }
	
	
	@Override
	public NetworkServer close()
	{
		for (Socket sock : opened)
			try
			{
				sock.close();
			} catch (Throwable t)
			{  }
		opened.clear();
		
		try
		{
			serverSocket.close();
			return this;
		} catch (IOException ex)
		{
			throw new NetworkException("Failed to close server socket", ex);
		}
	}

	@Override
	public boolean isAlive()
	{ return !serverSocket.isClosed(); }
}
