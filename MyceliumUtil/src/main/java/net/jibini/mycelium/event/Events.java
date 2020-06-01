package net.jibini.mycelium.event;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.mycelium.error.HandlerException;
import net.jibini.mycelium.error.MissingResourceException;

public class Events
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private Map<String, Spawnable> spawnPoints = new ConcurrentHashMap<>();
	private Map<String, Spawnable> spawned = new ConcurrentHashMap<>();
	
	public Spawnable spawnHandler(String eventType)
	{
		if (!spawnPoints.containsKey(eventType))
			throw new MissingResourceException("No spawn point for event '" + eventType + "'");
		
		log.debug("Spawning new event handler for '" + eventType + "'");
		return spawnPoints.get(eventType).spawn();
	}
	
	public Events handleEvent(Event event)
	{
		if (!spawned.containsKey(event.parentSpawnableName()))
			spawned.put(event.parentSpawnableName(), spawnHandler(event.type()));
		
		Spawnable handler = spawned.get(event.parentSpawnableName());
		invokeHandlerMethod(handler, event);
		return this;
	}
	
	// Reflection is a necessary evil.
	private void invokeHandlerMethod(Spawnable handler, Event event)
	{
		Method[] methods = handler.getClass().getMethods();
		boolean found = false;
		
		for (Method m : methods)
		{
			Handles[] annotations = m.getAnnotationsByType(Handles.class);
			for (Handles h : annotations)
				if (h.value().equals(event.type()))
					try
					{
						found = true;
						System.out.println(m.getParameterCount() + m.getParameterTypes()[0].getName());
						m.invoke(handler, event);
					} catch (Throwable t)
					{
						throw new HandlerException("Failed to invoke event handler", t);
					}
		}
		
		if (!found)
			throw new MissingResourceException("Unexpected event type for spawnable '" + event.type() + "'");
	}
	
	public Events registerSpawnPoint(String eventType, Spawnable point)
	{ spawnPoints.put(eventType, point); return this; }
}
