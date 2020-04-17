package net.jibini.mycelium.spore;

import net.jibini.mycelium.conf.ConfigFile;
import net.jibini.mycelium.routing.StitchLink;

public interface Spore
{
	ConfigFile generalConfig();
	
	ConfigFile runtimeConfig();
	
	StitchLink uplink();
	
	Spore start();
}
