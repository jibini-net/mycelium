package net.jibini.mycelium.route;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.jibini.mycelium.api.InternalRequest;
import net.jibini.mycelium.link.StitchPatch;
import net.jibini.mycelium.network.NetworkAdapter;
import net.jibini.mycelium.network.NetworkException;

public class TestNetworkAdapter
{
	private ServerSocket server;
	
	@Before
	public void startServer() throws IOException
	{
		server = new ServerSocket();
		server.bind(new InetSocketAddress("0.0.0.0", 25605));
		
		new Thread(() ->
		{
			try
			{
				Socket connection = server.accept();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				
				String read = reader.readLine();
				writer.write(read);
				writer.write('\n');
				writer.flush();
			} catch (Exception ex)
			{  }
		}).start();
	}
	
	@Test(timeout=2000)
	public void testConnectAndEcho() throws UnknownHostException, IOException, InterruptedException
	{
		Socket socket = new Socket("127.0.0.1", 25605);
		NetworkAdapter adapter = new NetworkAdapter()
				.withName("Adapter")
				.withSocket(socket);
		StitchPatch patch = new StitchPatch()
				.withName("Patch");
		new RequestSwitch()
				.routeBy("target")
				.attach(adapter)
				.attach(patch);
		
		InternalRequest req = new InternalRequest()
				.withTarget("Adapter");
		patch.send(req);
		assertEquals("Adapter", patch.read().header().getString("target"));
		
		adapter.close();
		patch.close();
		Thread.sleep(100);
	}
	
	@Test(timeout=2000, expected=NetworkException.class)
	public void testNotGivenSocket() throws UnknownHostException, IOException, InterruptedException
	{
		NetworkAdapter adapter = new NetworkAdapter()
				.withName("Adapter");
		StitchPatch patch = new StitchPatch()
				.withName("Patch");
		new RequestSwitch()
				.routeBy("target")
				.attach(adapter)
				.attach(patch);

		Thread.sleep(20);
		patch.close();
		Thread.sleep(100);
		adapter.read();
	}
	
	@After
	public void stopServer()
	{
		try
		{
			server.close();
		} catch (IOException ex)
		{  }
	}
}
