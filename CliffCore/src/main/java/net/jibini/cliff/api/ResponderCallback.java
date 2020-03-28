package net.jibini.cliff.api;

import net.jibini.cliff.routing.Request;
import net.jibini.cliff.routing.RequestCallback;
import net.jibini.cliff.routing.RequestRouter;
import net.jibini.cliff.routing.StitchLink;

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
		request.getHeader().put("target", RequestRouter.RESPONSE_TARGET);
		if (responder.respond(source, request))
			source.sendRequest(request);
	}
}
