package net.jibini.mycelium.network;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import net.jibini.mycelium.Mycelium;
import net.jibini.mycelium.api.RequestHandler;
import net.jibini.mycelium.conf.ConfigFile;
import net.jibini.mycelium.plugin.AbstractSpore;
import net.jibini.mycelium.plugin.PluginManager;
import net.jibini.mycelium.routing.AsyncPatch;
import net.jibini.mycelium.routing.Patch;
import net.jibini.mycelium.routing.RequestRouter;
import net.jibini.mycelium.routing.StitchLink;

public class NetworkingSpore extends AbstractSpore
{
	private Logger log;
	
	private ConfigFile networkConfig;
	private RequestRouter router;
	
	private void createConfigs() throws IOException
	{
			networkConfig = new ConfigFile()
					.at("config/network.json")
					.load()
					.defaultValue("tick-millis", 500)
					
					.pushMap("node-bind")
						.defaultValue("address", "0.0.0.0")
						.defaultValue("port", 25605)
					.pop()
					
					.pushMap("link")
						.defaultValue("max-connections", 100)
						.defaultValue("persistence-ticks", 10)
						.defaultValue("close-timeout-ticks", 2)
					.pop()
					
					.defaultValue("redirects", new JSONArray()
							.put(new JSONObject()
									.put("target", "ExampleTarget")
									.put("address", "127.0.0.1")
									.put("port", 25605)))
					.write()
					.close();
	}
	
	@Override
	public void create(PluginManager master, JSONObject manifest, StitchLink uplink)
	{
		log = getLogger();
		router = master.getPluginRouter();
		
		try
		{
			log.debug("Creating default configs");
			createConfigs();
		} catch (IOException ex)
		{
			throw new RuntimeException("Failed to create configs", ex);
		}
		
		super.create(master, manifest, uplink);
	}
	
	public void registerRedirect(String target, String address, int port)
	{
		Patch redirect = AsyncPatch.create();
		router.registerEndpoint(target, redirect.getDownstream());
		//TODO: Throw patch upstream to network handling
		log.info("Registered network redirect from target '" + target + "' to node '" + address + ":" + port + "'");
	}
	
	@Override
	public void registerRequests(RequestHandler requestHandler)
	{
		requestHandler.attachRequestCallback("RegisterRedirectRequest", (s, r) ->
		{
			registerRedirect(r.body().getString("target"), r.body().getString("address"), r.body().getInt("port"));
		});
		
		JSONArray redirects = networkConfig.valueJSONArray("redirects");
		for (int i = 0; i < redirects.length(); i ++)
			try
			{
				JSONObject t = redirects.getJSONObject(i);
				registerRedirect(t.getString("target"), t.getString("address"), t.getInt("port"));
			} catch (Throwable t)
			{
				log.error("Failed to register redirect for entry #" + i, t);
			}
	}

	@Override
	public void start()
	{
		
	}
	
	// Test entry point
	public static void main(String[] args)
	{
		try
		{
			NetworkingSpore instance = new NetworkingSpore();
			Mycelium.instance().getPluginManager().registerPlugin(instance, PluginManager.getPluginManifest(instance.getClass().getClassLoader()));
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
		
		new Thread(() ->
		{
			try
			{
				Thread.sleep(15000);
				System.err.println("Test period timeout");
				
				Mycelium.kill();
			} catch (InterruptedException ex)
			{ }
		}).start();
		
		Mycelium.main(args);
	}
}
