package net.jibini.mycelium.spore;

import net.jibini.mycelium.conf.ConfigFile;
import net.jibini.mycelium.link.StitchLink;

public interface Spore
{
	ConfigFile generalConfig();
	
	StitchLink uplink();
	
	Spore start();
}
