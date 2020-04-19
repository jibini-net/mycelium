package net.jibini.mycelium.api;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.mycelium.error.InteractionException;

public final class RoutedInteractions implements Interactions
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private Map<String, Interaction> startPoints = new ConcurrentHashMap<>();
	private Map<String, Interaction> active = new ConcurrentHashMap<>();

	@Override
	public Interaction spawnInteraction(String start)
	{
			if (!startPoints.containsKey(start))
				throw new InteractionException("Unexpected request name '" + start + "'");
			
			log.debug("Spawning new interaction for request '" + start + "'");
			return startPoints.get(start).spawn();
	}

	@Override
	public Interaction continueInteraction(Request request)
	{
		if (!request.header().has("interaction"))
			throw new InteractionException("Supplied request does not specify an interaction UUID");
		if (!request.header().has("request"))
			throw new InteractionException("Supplied request does not specify a request name");
		String interaction = request.header().getString("interaction");
		String reqName = request.header().getString("request");
		
		if (!active.containsKey(interaction))
			active.put(interaction, spawnInteraction(reqName));
		
		Interaction act = active.get(interaction);
		invokeHandlerMethod(act, request);
		return active.get(interaction);
	}
	
	// Reflection is a necessary evil.
	private void invokeHandlerMethod(Interaction interaction, Request request)
	{
		String reqName = request.header().getString("request");
		Method[] methods = interaction.getClass().getMethods();
		boolean found = false;
		
		for (Method m : methods)
		{
			Handles[] annotations = m.getAnnotationsByType(Handles.class);
			for (Handles h : annotations)
				if (h.value().equals(reqName))
					try
					{
						found = true;
						m.invoke(interaction, request);
					} catch (Throwable t)
					{
						throw new InteractionException("Failed to invoke request handle", t);
					}
		}
		
		if (!found)
			throw new InteractionException("Unexpected request for current interaction '" + reqName + "'");
	}
	
	public RoutedInteractions registerStartPoint(String requestName, Interaction spawned) { startPoints.put(requestName, spawned); return this; }
}
