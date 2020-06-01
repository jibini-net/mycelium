package net.jibini.mycelium.api;

import net.jibini.mycelium.error.InteractionException;
import net.jibini.mycelium.event.Events;
import net.jibini.mycelium.link.StitchLink;

public class Interactions
{
	private Events events = new Events();

	public Interactions continueInteraction(Request request, StitchLink source)
	{
		if (!request.header().has("interaction"))
			throw new InteractionException("Supplied request does not specify an interaction UUID");
		if (!request.header().has("request"))
			throw new InteractionException("Supplied request does not specify a request name");
		//TODO: Session cache, add data/reference to event
		
		RequestEvent event = new RequestEvent()
				.from(request)
				.withSource(source);
		events.handleEvent(event);
		
		return this;
	}
	
	public Interactions registerStartPoint(String requestName, Interaction spawned)
	{ events.registerSpawnPoint(requestName, spawned); return this; }
}
