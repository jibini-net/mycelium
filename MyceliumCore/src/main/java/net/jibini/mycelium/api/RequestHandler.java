package net.jibini.mycelium.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.jibini.mycelium.routing.RequestCallback;
import net.jibini.mycelium.routing.StitchLink;

public class RequestHandler implements RequestCallback
{
	private Map<String, RequestCallback> callbacks = new HashMap<>();
	private List<RequestCallback> otherCallbacks = new ArrayList<>();

	private RequestHandler()
	{}

	public static RequestHandler create()
	{
		return new RequestHandler();
	}

	@Override
	public void onRequest(StitchLink source, Request request)
	{
		if (request.header().has("request"))
		{
			String req = request.header().getString("request");

			synchronized (callbacks)
			{
				if (callbacks.containsKey(req))
				{
					RequestCallback callback = callbacks.get(req);
					
					if (callback == null)
						triggerOtherCallbacks(source, request);
					else
						callback.onRequest(source, request);
				} else
					triggerOtherCallbacks(source, request);
			}
		} else
			triggerOtherCallbacks(source, request);
	}

	private void triggerOtherCallbacks(StitchLink source, Request request)
	{
		synchronized (otherCallbacks)
		{
			for (RequestCallback callback : otherCallbacks)
				callback.onRequest(source, request);
		}
	}

	public void attachRequestCallback(String request, RequestCallback callback)
	{
		if (request == null)
			synchronized (otherCallbacks)
			{
				otherCallbacks.add(callback);
			}
		else
			synchronized (callbacks)
			{
				callbacks.put(request, callback);
			}
	}
}
