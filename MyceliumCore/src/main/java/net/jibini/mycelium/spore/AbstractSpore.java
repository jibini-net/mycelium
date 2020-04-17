package net.jibini.mycelium.spore;

import java.io.IOException;

import net.jibini.mycelium.conf.ConfigFile;
import net.jibini.mycelium.error.ConfigurationException;
import net.jibini.mycelium.routing.StitchLink;

public abstract class AbstractSpore implements Spore
{
	private ConfigFile generalConfig = new ConfigFile()
			.at("config/" + profile().serviceName() + ".json");
	private ConfigFile runtimeConfig = new ConfigFile();
	
	@Override
	public ConfigFile generalConfig()
	{
		if (!generalConfig.isCached())
			try
			{
				generalConfig.load()
						
						.pushMap("spore")
							.defaultValue("node-name", profile().serviceName())
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

	@Override
	public ConfigFile runtimeConfig() { return runtimeConfig.defaultValue("spore", false); }
	

	@Override
	public StitchLink uplink()
	{
		return null;
	}
	
	public abstract SporeProfile profile();
}
