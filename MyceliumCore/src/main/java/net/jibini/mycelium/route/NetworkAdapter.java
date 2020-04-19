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

public final class NetworkAdapter extends AbstractNetworkMember<NetworkAdapter>
		implements StitchLink
{
	private Socket socket;
	private boolean hasSocket = false;
	private BufferedReader reader;
	private BufferedWriter writer;
	
	public NetworkAdapter withSocket(Socket socket)
	{
		try
		{
			this.socket = socket;
			writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			hasSocket = true;
			return this;
		} catch (IOException ex)
		{
			throw new NetworkException("Failed to initiate network adapter", ex);
		}
	}

	@Override
	public StitchLink link() { return this; }
	

	@Override
	public StitchLink send(Request request)
	{
		try
		{
			if (!hasSocket)
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
			if (!hasSocket)
				throw new MissingResourceException("Adapter was not given a socket");
			String line = reader.readLine();
			
			if (line == null)
			{
				close();
				throw new NetworkException("Network adapter is closing");
			}
			
			return new InternalRequest().from(line);
		} catch (SocketException ex)
		{
			close();
			throw new NetworkException("Network adapter is closing", ex);
		} catch (Exception ex)
		{
			throw new NetworkException("Failed to read from network adapter", ex);
		}
	}

	@Override
	public boolean isAlive()
	{
		if (!hasSocket)
			return false;
		return !socket.isClosed();
	}

	@Override
	public StitchLink close()
	{
		try
		{
			if (!hasSocket)
				throw new MissingResourceException("Adapter was not given a socket");
			socket.close();
			return this;
		} catch (Exception ex)
		{
			throw new NetworkException("Failed to close network adapter", ex);
		}
	}
}
