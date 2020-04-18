package net.jibini.mycelium.api;

import net.jibini.mycelium.routing.StitchLink;

// TODO
public final class ReceivedRequest extends AbstractRequest<ReceivedRequest>
{
	@FunctionalInterface
	public interface Responder
	{
		boolean respond(ReceivedRequest received, Response response);
	}
	
	public StitchLink source()
	{
		return null;
	}
	
	public StitchLink respond(Responder responder)
	{
		Response response = new Response();
		// Fill header, etc.
		
		if (responder.respond(this, response)); // (Send)
		return source();
	}
}
