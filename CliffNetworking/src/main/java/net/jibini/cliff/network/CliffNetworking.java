package net.jibini.cliff.network;

import java.io.IOException;

import org.json.JSONObject;
import org.slf4j.Logger;

import net.jibini.cliff.Cliff;
import net.jibini.cliff.ConfigFile;
import net.jibini.cliff.api.RequestHandler;
import net.jibini.cliff.plugin.AbstractCliffPlugin;
import net.jibini.cliff.plugin.PluginManager;
import net.jibini.cliff.plugin.PluginRouter;
import net.jibini.cliff.routing.AsyncPatch;
import net.jibini.cliff.routing.Patch;
import net.jibini.cliff.routing.StitchLink;

public class CliffNetworking extends AbstractCliffPlugin
{
	private Logger log;
	
	private ConfigFile networkConfig;
	private PluginRouter router;
	
	private void createConfigs() throws IOException
	{
		{ /*			NETWORK CONFIG				*/
			networkConfig = ConfigFile.create("config/network.json");
			
			networkConfig.setDefault("tick-millis", 500);
			
			{ /*		BIND SECTION				*/
				networkConfig.setDefault("bind", new JSONObject());
				JSONObject bind = networkConfig.getJSONObject("bind");
				networkConfig.setDefault(bind, "address", "0.0.0.0");
				networkConfig.setDefault(bind, "port", 25605);
			}
			
			{ /*		LINK SECTION				*/
				networkConfig.setDefault("link", new JSONObject());
				JSONObject link = networkConfig.getJSONObject("link");
				networkConfig.setDefault(link, "max-connections", 100);
				networkConfig.setDefault(link, "allow-indefinite-persistence", true);
				networkConfig.setDefault(link, "persistence-ticks", 10);
				networkConfig.setDefault(link, "close-timeout-ticks", 2);
			}
			
			{ /*		REDIRECT SECTION			*/
				JSONObject redirects = new JSONObject();
				redirects.put("TargetName", "CliffNodeName");
				networkConfig.setDefault("redirects", redirects);
			}
			
			networkConfig.writeConfig();
		}
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
	
	public void registerRedirect(String target, String cliffNode)
	{
		Patch redirect = AsyncPatch.create();
		router.registerEndpoint(target, redirect.getDownstream());
		//TODO: Throw patch upstream to network handling 
		log.info("Registered network redirect from target '" + target + "' to node '" + cliffNode + "'");
	}
	
	@Override
	public void registerRequests(RequestHandler requestHandler)
	{
		requestHandler.attachRequestCallback("RegisterRedirectRequest", (s, r) ->
		{
			registerRedirect(r.getBody().getString("target"), r.getBody().getString("cliff-node"));
		});
		
		JSONObject redirects = networkConfig.getJSONObject("redirects");
		for (String target : redirects.keySet())
			registerRedirect(target, redirects.getString(target));
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
			CliffNetworking instance = new CliffNetworking();
			Cliff.getInstance().getPluginManager().registerPlugin(instance, PluginManager.getPluginManifest(instance.getClass().getClassLoader()));
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
		
		Cliff.main(args);
	}
}
