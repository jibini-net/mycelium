package net.jibini.cliff.api;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.cliff.plugin.AbstractCliffPlugin;
import net.jibini.cliff.plugin.CliffPlugin;
import net.jibini.cliff.plugin.PluginManager;
import net.jibini.cliff.routing.AsyncPatch;
import net.jibini.cliff.routing.Patch;
import net.jibini.cliff.routing.Request;
import net.jibini.cliff.routing.StitchLink;

public class TestResponses
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private int read = 0;
	
	@Test
	public void testResponder() throws InterruptedException
	{
		PluginManager manager = PluginManager.create();
		
		CliffPlugin testPlugin0 = new CliffPlugin()
		{
			@Override
			public void create(PluginManager master, JSONObject manifest, StitchLink uplink)
			{
				log.debug("Test plugin 0 create . . .");
				
				uplink.addPersistentCallback(ResponderCallback.create((req) ->
				{
					log.debug(req.toString());
					req.getResponse().put("Hello", "World");
					read++;
					
					return true;
				}));
			}
		};
		
		JSONObject manifest0 = new JSONObject();
		manifest0.put("name", "TestPlugin0");
		manifest0.put("version", "1.0");
		manager.registerPlugin(testPlugin0, manifest0);
		Thread.sleep(30);
		
		CliffPlugin testPlugin1 = new CliffPlugin()
		{
			@Override
			public void create(PluginManager master, JSONObject manifest, StitchLink uplink)
			{
				log.debug("Test plugin 1 create . . .");
				
				uplink.sendRequest(Request.create("TestPlugin0", "TestRequest", new JSONObject()));
				
				uplink.readRequest((source, req) ->
				{
					log.debug(req.toString());
					read++;
				});
			}
		};

		JSONObject manifest1 = new JSONObject();
		manifest1.put("name", "TestPlugin1");
		manifest1.put("version", "2.0");
		manager.registerPlugin(testPlugin1, manifest1);
		
		Thread.sleep(30);
		assertEquals("Request callback did not trigger", 2, read);
	}
	
	@Test
	public void testAbstractPluginResponses() throws InterruptedException
	{
		PluginManager manager = PluginManager.create();
		
		CliffPlugin testPlugin = new AbstractCliffPlugin()
		{
			@Override
			public void registerRequests(RequestHandler requestHandler)
			{
				requestHandler.attachRequestCallback("Request", ResponderCallback.create((req) ->
				{
					req.getResponse().put("value", 1337);
					
					return true;
				}));
			}

			@Override
			public void start()
			{
				getLogger().debug("Test plugin start . . .");
			}
		};
		
		JSONObject manifest = new JSONObject();
		manifest.put("name", "TestPlugin");
		manifest.put("version", "3.0");
		manager.registerPlugin(testPlugin, manifest);
		manager.notifyPluginStart();
		
		Patch sender = AsyncPatch.create();
		manager.getPluginRouter().registerEndpoint("Sender", sender.getUpstream());
		sender.getDownstream().sendRequest(Request.create("TestPlugin", "Request"));
		sender.getDownstream().readRequest((s, r) ->
		{
			log.debug(r.toString());
			read = r.getResponse().getInt("value");
		});
		
		Thread.sleep(20);
		assertEquals("Request callback did not trigger", 1337, read);
	}
}
