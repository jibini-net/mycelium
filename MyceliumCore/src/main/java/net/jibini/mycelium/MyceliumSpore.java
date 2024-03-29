package net.jibini.mycelium;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.mycelium.api.Request;
import net.jibini.mycelium.api.RoutedInteractions;
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
			.attach(selfPatch);
//			.withDefaultGateway(selfPatch);
	private boolean isAlive = false;
	
	private RoutedInteractions interactions = new RoutedInteractions()
			.registerStartPoint("ServiceAvailable", new MyceliumInteractions.ServiceAvailable());
	
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
	
	private void update()
	{
		Request request;
		
		try
		{
			request = uplink().read();
		} catch (Throwable t)
		{
			if (System.getProperties().getOrDefault("verboseNetworking", false).equals("true"))
				log.warn("Could not read request from uplink", t);
			return;
		}
		
		try
		{
			interactions.continueInteraction(request, uplink());
		} catch (Throwable t)
		{
			//TODO: Error responses
			log.error("Could not continue request interaction", t);
		}
	}
	
	@Override
	public MyceliumSpore start()
	{
		log.info("Starting " + profile().serviceName() + " (" + profile().version() + ")");
		startServer();
		isAlive = true;
		
		while (isAlive)
			update();
		return this;
	}

	public SporeProfile profile() { return new MyceliumProfile(); }
	
	public NetworkServer server() { return server; }
	

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
							.defaultValue("address", "0.0.0.0")
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
	
	
	public void close()
	{
		log.info("Shutting down . . .");
		isAlive = false;
		selfPatch.close();
		server.close();
		log.info("Good-bye!");
	}
}
