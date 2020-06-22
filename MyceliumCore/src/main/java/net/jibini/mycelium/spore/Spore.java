package net.jibini.mycelium.spore;

import net.jibini.mycelium.conf.ConfigFile;
import net.jibini.mycelium.link.StitchLink;

public interface Spore
{
	static final String HOOK_UPLINK = "uplink";
	static final String HOOK_SERVICE_AVAILABLE = "serviceAvailable";
	
	static final String HOOK_REQUEST_RECEIVED = "requestReceived";
	
	ConfigFile generalConfig();
	
	StitchLink uplink();
	
	Spore start();
}
