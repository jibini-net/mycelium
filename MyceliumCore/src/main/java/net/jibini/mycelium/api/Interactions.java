package net.jibini.mycelium.api;

import net.jibini.mycelium.link.StitchLink;

public interface Interactions
{
	Interaction spawnInteraction(String start);
	
	Interaction continueInteraction(Request request, StitchLink source);
}
