package net.jibini.mycelium.route;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

import net.jibini.mycelium.api.InternalRequest;
import net.jibini.mycelium.api.Request;
import net.jibini.mycelium.error.MissingResourceException;
import net.jibini.mycelium.error.NetworkException;
import net.jibini.mycelium.link.StitchLink;
import net.jibini.mycelium.resource.Checked;

public final class NetworkAdapter extends AbstractNetworkMember<NetworkAdapter>
		implements StitchLink
{
	private Checked<Socket> socket = new Checked<Socket>()
			.withName("Socket");
	private BufferedReader reader;
	private BufferedWriter writer;
	
	private boolean embedInteraction = false;
	
	public NetworkAdapter withSocket(Socket socket)
	{
		try
		{
			this.socket.value(socket);
			writer = new BufferedWriter(new OutputStreamWriter(this.socket.value().getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(this.socket.value().getInputStream()));
			return this;
		} catch (IOException ex)
		{
			throw new NetworkException("Failed to initiate network adapter", ex);
		}
	}
	
	public NetworkAdapter connect(String address, int port)
	{
		try
		{
			return withSocket(new Socket(address, port));
		} catch (IOException ex)
		{
			throw new NetworkException("Failed to open new socket", ex);
		}
	}

	@Override
	public StitchLink link() { return this; }
	

	@Override
	public StitchLink send(Request request)
	{
		try
		{
			if (!socket.has())
				throw new MissingResourceException("Adapter was not given a socket");
			writer.write(request.toString());
			writer.write('\n');
			writer.flush();
			return this;
		} catch (Exception ex)
		{
			throw new NetworkException("Failed to write to network adapter", ex);
		}
	}

	@Override
	public Request read()
	{
		try
		{
			if (!socket.has())
				throw new MissingResourceException("Adapter was not given a socket");
			String line = reader.readLine();
			
			if (line == null)
			{
				close();
				throw new NetworkException("Network adapter is closing");
			}
			
			InternalRequest request = new InternalRequest().from(line);
			if (embedInteraction)
				request.header().put("interaction", uuid().toString());
			return request;
		} catch (SocketException ex)
		{
			close();
			throw new NetworkException("Network adapter is closing", ex);
		} catch (Exception ex)
		{
			throw new NetworkException("Failed to read from network adapter", ex);
		}
	}
	
	public NetworkAdapter embedInteraction() { this.embedInteraction = true; return this; }
	

	@Override
	public boolean isAlive()
	{
		if (!socket.has())
			return false;
		return !socket.value().isClosed();
	}

	@Override
	public StitchLink close()
	{
		try
		{
			socket.value().close();
			return this;
		} catch (Exception ex)
		{
			throw new NetworkException("Failed to close network adapter", ex);
		}
	}
}
