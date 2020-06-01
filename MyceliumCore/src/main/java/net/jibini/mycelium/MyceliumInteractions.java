package net.jibini.mycelium;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.mycelium.api.Interaction;
import net.jibini.mycelium.api.RequestEvent;
import net.jibini.mycelium.event.Handles;

public class MyceliumInteractions
{
	private static final MyceliumSpore SPORE = Mycelium.SPORE;
	private static final Logger LOG = LoggerFactory.getLogger(MyceliumInteractions.class);
	
	public static class ServiceAvailable implements Interaction
	{
		@Override
		public Interaction spawn()
		{ return new ServiceAvailable(); }
		
		
		@Handles("ServiceAvailable")
		public void serviceAvailable(RequestEvent event)
		{
			//TODO: Check secret key
			String target = event.request().body().getString("target");
			JSONObject route = event.request().header().getJSONObject("route");
			SPORE.server().router().staticRoute(target, route);
			
			LOG.info("Registered static route for '" + target + "'");
			LOG.debug(route.toString(4));
		}
	}
}
