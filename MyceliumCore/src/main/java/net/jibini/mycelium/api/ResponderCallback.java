package net.jibini.mycelium.api;

import net.jibini.mycelium.routing.RequestCallback;
import net.jibini.mycelium.routing.RequestRouter;
import net.jibini.mycelium.routing.StitchLink;

@Deprecated
public class ResponderCallback implements RequestCallback
{
	private Responder responder;
	
	public static ResponderCallback create(Responder responder)
	{
		ResponderCallback result = new ResponderCallback();
		result.responder = responder;
		return result;
	}

	@Override
	public void onRequest(StitchLink source, Request request)
	{
		request.header().put("target", RequestRouter.RESPONSE_TARGET);
		if (responder.respond(source, request))
			source.sendRequest(request);
	}
}
