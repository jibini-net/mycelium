package net.jibini.cliff.routing;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestRouter
{
	public static final String UPSTREAM_NAME = "Upstream";
	public static final String RESPONSE_TARGET = "Response";
	
	private Logger log = LoggerFactory.getLogger(getClass());
	private Map<String, StitchLink> endpoints = new HashMap<>();
	
	private String headerElement;
	private boolean upstream;
	
	private RequestRouter()
	{}
	
	public static RequestRouter create(String headerElement, boolean upstream)
	{
		RequestRouter result = new RequestRouter();
		result.headerElement = headerElement;
		result.upstream = upstream;
		return result;
	}
	
	public static RequestRouter create(String headerElement)
	{
		return create(headerElement, false);
	}
	
	public void registerEndpoint(String name, StitchLink link)
	{
		synchronized (endpoints)
		{
			endpoints.put(name, link);
			log.debug("Registered new endpoint '" + name + "'");
		}
		
		link.addPersistentCallback((source, request) ->
		{
			try
			{
				String target = request.getHeader().getString("target");
				String element = request.getHeader().getString(headerElement);
				JSONArray route = request.getHeader().getJSONArray("route");
				
				if (target.equals(RESPONSE_TARGET))
				{
					int index = route.toList().size() - 1;
					element = route.getString(index);
					route.remove(index);
				} else
					route.put(name);
				
//				log.debug("ROUTE " + element);
//				log.debug(request.toString());
				
				if (endpoints.containsKey(element))
				{
					StitchLink endpoint = endpoints.get(element);
					endpoint.sendRequest(request);
				} else if (upstream && endpoints.containsKey(UPSTREAM_NAME))
				{
					endpoints.get(UPSTREAM_NAME).sendRequest(request);
					log.debug("Resorting to upstream connection");
				} else
					throw new RuntimeException("No endpoint found for target '" + element + "'");
			} catch (Throwable t)
			{
				log.error("Failed to route plugin request", t);
			}
		});
	}
}
