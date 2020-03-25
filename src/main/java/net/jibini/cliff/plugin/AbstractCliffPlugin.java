package net.jibini.cliff.plugin;

import net.jibini.cliff.api.RequestHandler;
import net.jibini.cliff.routing.StitchLink;

public abstract class AbstractCliffPlugin implements CliffPlugin
{
	private RequestHandler requestHandler = RequestHandler.create();
	
	@Override
	public void create(StitchLink uplink)
	{
		uplink.addPersistentCallback(requestHandler);
		
		registerRequests(requestHandler);
		start();
	}
	
	public abstract void registerRequests(RequestHandler requestHandler);
	
	public abstract void start();
	
	public RequestHandler getDefaultRequestHandler()  { return requestHandler; }
}
