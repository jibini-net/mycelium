package net.jibini.cliff.plugin;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.cliff.routing.StitchLink;

public class PluginRouter
{
	private Logger log = LoggerFactory.getLogger(getClass());
	private Map<String, StitchLink> endpoints = new HashMap<>();
	
	private PluginRouter()
	{}
	
	public static PluginRouter create()
	{
		PluginRouter result = new PluginRouter();
		return result;
	}
	
	public void registerEndpoint(String name, StitchLink link)
	{
		synchronized (endpoints)
		{
			endpoints.put(name, link);
			log.info("Registered new endpoint '" + name + "'");
		}
		
		link.addPersistentCallback((source, request) ->
		{
			try
			{
				String target = request.getHeader().getString("target");
				
				// Alternate router-side response implementation
//				if (target.equals("Response"))
//				{
//					if (request.getHeader().has("origin"))
//						target = request.getHeader().getString("origin");
//				} else
					request.getHeader().put("origin", name);
					
					
				if (endpoints.containsKey(target))
				{
					StitchLink endpoint = endpoints.get(target);
					endpoint.sendRequest(request);
				} else
					throw new RuntimeException("No endpoint found for target '" + target + "'");
			} catch (Throwable t)
			{
				log.error("Failed to route plugin request", t);
			}
		});
	}
}
