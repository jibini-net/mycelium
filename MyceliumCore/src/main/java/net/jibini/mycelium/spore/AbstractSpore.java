package net.jibini.mycelium.spore;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.mycelium.api.Request;
import net.jibini.mycelium.api.RoutedInteractions;
import net.jibini.mycelium.conf.ConfigFile;
import net.jibini.mycelium.error.ConfigurationException;
import net.jibini.mycelium.error.NetworkException;
import net.jibini.mycelium.link.StitchLink;
import net.jibini.mycelium.route.NetworkAdapter;

public abstract class AbstractSpore implements Spore
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private ConfigFile generalConfig = new ConfigFile()
			.at("config/" + profile().serviceName() + ".json");
	
	private NetworkAdapter uplink = new NetworkAdapter();
//	private RequestSwitch interactionSwitch = new RequestSwitch()
//			.routeBy("interaction")
//			.withDefaultGateway(uplink);
	private RoutedInteractions interactions = new RoutedInteractions();
	
	@Override
	public ConfigFile generalConfig()
	{
		if (!generalConfig.isCached())
			try
			{
				generalConfig.load()
						
						.pushMap("spore")
							.defaultValue("node-name", profile().serviceName())
							.defaultValue("interaction-timeout", 3600)
						.pop()
						
						.pushMap("uplink")
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
	
	public void connectUplink()
	{
		try
		{
			String addressName = generalConfig().pushMap("uplink").valueString("address");
			int port = generalConfig().pushMap("uplink").valueInt("port");
			
			log().info("Connecting to uplink '" + addressName + ':' + port + "' . . .");
			Socket socket = new Socket(addressName, port);
			log().debug("Successfully opened uplink connection");
			uplink.withSocket(socket);
//			interactionSwitch.attach(uplink);
		} catch (IOException ex)
		{
			throw new NetworkException("Failed to connect to uplink", ex);
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
			log().warn("Could not read request from uplink", t);
			return;
		}
		
		try
		{
			interactions.continueInteraction(request);
		} catch (Throwable t)
		{
			//TODO: Error responses
			log().error("Could not continue request interaction", t);
		}
	}
	
	@Override
	public AbstractSpore start()
	{
		log().info("Starting spore " + profile().serviceName() + " (" + profile().version() + ") . . .");
		connectUplink();
		postUplink();
		while (uplink().isAlive())
			update();
		return this;
	}
	

	@Override
	public StitchLink uplink() { return uplink; }
	
	public Logger log() { return log; }
	
	public RoutedInteractions interactions() { return interactions; }
	
	
	public abstract SporeProfile profile();
	
	public abstract void postUplink();
}
