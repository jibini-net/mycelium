package net.jibini.mycelium.plugin;

import org.json.JSONObject;

import net.jibini.mycelium.routing.StitchLink;

@Deprecated
@FunctionalInterface
public interface MyceliumSpore
{
	void create(PluginManager master, JSONObject manifest, StitchLink uplink);
}
