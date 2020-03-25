package net.jibini.cliff.api;

import org.json.JSONObject;

import net.jibini.cliff.routing.Request;
import net.jibini.cliff.routing.RequestCallback;
import net.jibini.cliff.routing.StitchLink;

public class ResponderCallback implements RequestCallback
{
	private String defaultResponse;
	private Responder responder;
	
	public static ResponderCallback create(String defaultResponse, Responder responder)
	{
		ResponderCallback result = new ResponderCallback();
		result.defaultResponse = defaultResponse;
		return result;
	}

	@Override
	public void onRequest(StitchLink source, Request request)
	{
		//TODO: Currently creates infinite routing loop.  Link into session,
		//		or create an endpoint called SessionConnector
		String target = request.getHeader().getString("target");
		Request response = Request.create(defaultResponse, target, new JSONObject());
		responder.respond(request, response);
		source.sendRequest(response);
	}
}
