package net.jibini.cliff.plugin;

import org.json.JSONObject;

import net.jibini.cliff.routing.StitchLink;

@FunctionalInterface
public interface CliffPlugin
{
	void create(PluginManager master, JSONObject manifest, StitchLink uplink);
}
