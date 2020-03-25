package net.jibini.cliff.plugin;

import net.jibini.cliff.routing.StitchLink;

@FunctionalInterface
public interface CliffPlugin
{
	void create(StitchLink uplink);
}
