package net.jibini.mycelium.spore;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.mycelium.api.InternalRequest;
import net.jibini.mycelium.api.Request;
import net.jibini.mycelium.api.Interactions;
import net.jibini.mycelium.conf.ConfigFile;
import net.jibini.mycelium.conf.ConfigurationException;
import net.jibini.mycelium.hook.Hooks;
import net.jibini.mycelium.link.StitchLink;
import net.jibini.mycelium.network.NetworkAdapter;
import net.jibini.mycelium.network.NetworkException;

public abstract class AbstractSpore implements Spore
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private ConfigFile generalConfig = new ConfigFile()
			.at("config/" + profile().serviceName() + ".json");
	
	private NetworkAdapter uplink = new NetworkAdapter();
//	private RequestSwitch interactionSwitch = new RequestSwitch()
//			.routeBy("interaction")
//			.withDefaultGateway(uplink);
	private Interactions interactions = new Interactions();
	
	private Hooks hooks = new Hooks();
	
	public AbstractSpore()
	{ registerHooks(this); }
	
	@Override
	public ConfigFile generalConfig()
	{
		if (!generalConfig.isCached())
			try
			{
				generalConfig.load();
						
				generalConfig.map("spore")
							.defaultValue("node-name", profile().serviceName())
							.defaultValue("interaction-timeout", 3600);
						
				generalConfig.map("uplink")
							.defaultValue("address", "127.0.0.1")
							.defaultValue("port", 25605)
							.defaultValue("secret", "");
						
				generalConfig.write()
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
			String addressName = generalConfig().map("uplink").<String>value("address");
			int port = generalConfig().map("uplink").<Integer>value("port");
			
			log().info("Connecting to uplink '" + addressName + ':' + port + "' . . .");
			Socket socket = new Socket(addressName, port);
			log().debug("Successfully opened uplink connection");
			uplink.withSocket(socket);
//			interactionSwitch.attach(uplink);
		} catch (IOException ex)
		{
			throw new NetworkException("Failed to connect to uplink", ex);
		}
		
		hooks.callHooks(Spore.HOOK_UPLINK);
	}
	
	private void serviceAvailable()
	{
		Request serviceAvailable = new InternalRequest()
				.withTarget("Mycelium")
				.withRequest("ServiceAvailable");
		serviceAvailable.body()
				.put("target", generalConfig().map("spore").<String>value("node-name"))
				.put("service", profile().serviceName());
		uplink().send(serviceAvailable);
		
		hooks.callHooks(Spore.HOOK_SERVICE_AVAILABLE);
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
				log().warn("Could not read request from uplink", t);
			return;
		}
		
		hooks.callHooks(Spore.HOOK_REQUEST_RECEIVED, request);
		
		try
		{
			interactions.continueInteraction(request, uplink());
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
		serviceAvailable();
		
		while (uplink().isAlive())
			update();
		return this;
	}
	

	@Override
	public StitchLink uplink()
	{ return uplink; }
	
	public Logger log()
	{ return log; }
	
	public Interactions interactions()
	{ return interactions; }
	
	
	public abstract SporeProfile profile();
	
	public void registerHooks(Object hooks)
	{ this.hooks.registerHooks(hooks); }
}
