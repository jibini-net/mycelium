package net.jibini.mycelium.plugin;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.mycelium.api.RequestHandler;
import net.jibini.mycelium.routing.StitchLink;

public abstract class AbstractSpore implements MyceliumSpore
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private PluginManager master;
	private StitchLink uplink;
	private JSONObject manifest;
	private Thread pluginThread;
	
	private RequestHandler requestHandler = RequestHandler.create();
	
	@Override
	public void create(PluginManager master, JSONObject manifest, StitchLink uplink)
	{
		uplink.addPersistentCallback(requestHandler);
		
		this.master = master;
		this.uplink = uplink;
		this.manifest = manifest;
		
		log.debug("Registering requests for plugin . . .");
		registerRequests(requestHandler);

		pluginThread = new Thread(() ->
		{
			log.info("Ready");
			master.waitPluginStart();
			start();
		});
		
		pluginThread.setName(manifest.getString("name"));
		pluginThread.start();
	}
	
	public abstract void registerRequests(RequestHandler requestHandler);
	
	public abstract void start();
	
	public Logger getLogger()  { return log; }
	
	public RequestHandler getDefaultRequestHandler()  { return requestHandler; }
	
	public PluginManager getPluginManager()  { return master; }
	
	public StitchLink getUplink()  { return uplink; }
	
	public JSONObject getManifest()  { return manifest; }
}
