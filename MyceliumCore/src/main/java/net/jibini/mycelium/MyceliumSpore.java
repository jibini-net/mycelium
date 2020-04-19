package net.jibini.mycelium;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.mycelium.conf.ConfigFile;
import net.jibini.mycelium.error.ConfigurationException;
import net.jibini.mycelium.link.StitchLink;
import net.jibini.mycelium.link.StitchPatch;
import net.jibini.mycelium.route.RequestSwitch;
import net.jibini.mycelium.spore.Spore;
import net.jibini.mycelium.spore.SporeProfile;

public final class MyceliumSpore implements Spore
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private ConfigFile generalConfig = new ConfigFile()
			.at("config/" + profile().serviceName() + ".json");
	private StitchPatch selfPatch = new StitchPatch();
	private RequestSwitch nodes = new RequestSwitch()
			.routeBy("target")
			.attach(selfPatch);
	
	@Override
	public MyceliumSpore start()
	{
		log.info("Starting " + profile().serviceName() + " (" + profile().version() + ")");
		
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
						
						.pushMap("tcp-bind")
							.defaultValue("bind-tcp", true)
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
