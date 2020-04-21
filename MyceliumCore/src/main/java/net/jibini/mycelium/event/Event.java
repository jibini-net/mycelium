package net.jibini.mycelium.event;

public interface Event
{
	String type();
	
	String parentSpawnableName();
}
