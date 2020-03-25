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
		result.responder = responder;
		return result;
	}

	@Override
	public void onRequest(StitchLink source, Request request)
	{
		if (request.getHeader().has("origin"))
		{
			String target = request.getHeader().getString("origin");
			Request response = Request.create(target, defaultResponse, new JSONObject());
			if (responder.respond(request, response))
				source.sendRequest(response);
		} else
			throw new RuntimeException("Cannot respond to request, no 'origin' present");
	}
}
