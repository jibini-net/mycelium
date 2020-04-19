package net.jibini.mycelium.api;

public interface Interactions
{
	Interaction spawnInteraction(String start);
	
	Interaction continueInteraction(Request request);
}
