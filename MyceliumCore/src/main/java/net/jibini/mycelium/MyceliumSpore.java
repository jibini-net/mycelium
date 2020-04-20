package net.jibini.mycelium;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.mycelium.conf.ConfigFile;
import net.jibini.mycelium.error.ConfigurationException;
import net.jibini.mycelium.error.NetworkException;
import net.jibini.mycelium.link.StitchLink;
import net.jibini.mycelium.link.StitchPatch;
import net.jibini.mycelium.route.NetworkServer;
import net.jibini.mycelium.spore.Spore;
import net.jibini.mycelium.spore.SporeProfile;

public final class MyceliumSpore implements Spore
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private ConfigFile generalConfig = new ConfigFile()
			.at("config/" + profile().serviceName() + ".json");
	private StitchPatch selfPatch = new StitchPatch()
			.withName("Mycelium");
	
	private NetworkServer server = new NetworkServer()
			.embedInteraction()
			.attach(selfPatch)
			.withDefaultGateway(selfPatch);
	
	public void startServer()
	{
		try
		{
			String addressName = generalConfig().pushMap("bind").valueString("address");
			int port = generalConfig().pushMap("bind").valueInt("port");
			log.info("Starting spore server on '" + addressName + ':' + port + "' . . .");
			
			ServerSocket socket = new ServerSocket();
			socket.bind(new InetSocketAddress(addressName, port));
			server.withServerSocket(socket)
					.start();
		} catch (IOException ex)
		{
			throw new NetworkException("Failed to open server socket", ex);
		}
	}
	
	@Override
	public MyceliumSpore start()
	{
		log.info("Starting " + profile().serviceName() + " (" + profile().version() + ")");
		startServer();
		return this;
	}

	public SporeProfile profile()
	{
		return new MyceliumProfile();
	}

	@Override
	public ConfigFile generalConfig()
	{
		if (!generalConfig.isCached())
			try
			{
				generalConfig.load()
						
						.pushMap("spore")
							.defaultValue("node-name", "Mycelium")
						.pop()
						
						.pushMap("bind")
							.defaultValue("address", "127.0.0.1")
							.defaultValue("port", 25605)
							.defaultValue("secret", "")
						.pop()
						
						.write()
						.close();
			} catch (IOException ex)
			{
				throw new ConfigurationException("Failed to load spore config", ex);
			}
		
		return generalConfig;
	}

	@Override
	public StitchLink uplink() { return selfPatch; }
}
