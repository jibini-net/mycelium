package net.jibini.mycelium.spore;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.jibini.mycelium.api.Handles;
import net.jibini.mycelium.api.Interaction;
import net.jibini.mycelium.api.InternalRequest;
import net.jibini.mycelium.api.Request;

public class TestAbstractSporeUplink
{
	private static Request received = null;
	
	private ServerSocket server;
	
	public static class TestInteraction implements Interaction
	{
		@Override
		public Interaction spawn()
		{
			return new TestInteraction();
		}
		
		@Handles("TestRequest")
		public void testRequest(Request request)
		{
			received = request;
		}
	}

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
				
				Thread.sleep(500);
				connection.close();
			} catch (Exception ex)
			{  }
		}).start();
	}
	
	private SporeProfile testProfile = new SporeProfile()
			{
				@Override
				public String serviceName()
				{
					return "TestSpore";
				}

				@Override
				public String version()
				{
					return "1.0";
				}

				@Override
				public int protocolVersion()
				{
					return 1;
				}
			};
	
	@Test(timeout=1500)
	public void testUplinkEcho() throws InterruptedException
	{
		new AbstractSpore()
				{
					@Override
					public SporeProfile profile()
					{
						return testProfile;
					}

					@Override
					public void postUplink()
					{
						interactions().registerStartPoint("TestRequest", new TestInteraction());
						
						uplink().send(new InternalRequest()
								.withHeader("interaction", UUID.randomUUID())
								.withTarget("Endpoint")
								.withRequest("TestRequest"));
					}

					@Override
					public void postServiceAvailable()
					{
						
					}
				}.start();
			assertEquals("Endpoint", received.header().get("target"));
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
