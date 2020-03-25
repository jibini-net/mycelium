package net.jibini.cliff.plugin;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.cliff.api.RequestHandler;
import net.jibini.cliff.routing.AsyncPatch;
import net.jibini.cliff.routing.Patch;
import net.jibini.cliff.routing.Request;
import net.jibini.cliff.routing.StitchLink;

public class TestPlugin
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private int read = 0;
	
	@Test
	public void testRegisterPlugin()
	{
		PluginManager manager = PluginManager.create();
		
		CliffPlugin testPlugin = new CliffPlugin()
		{
			@Override
			public void create(StitchLink uplink)
			{
				
			}
		};
		
		JSONObject manifest = new JSONObject();
		manifest.put("name", "TestPlugin");
		manifest.put("version", "1.0");
		manager.registerPlugin(testPlugin, manifest);
	}
	
	@Test
	public void testPluginRouting() throws InterruptedException
	{
		PluginManager manager = PluginManager.create();
		
		CliffPlugin testPlugin = new CliffPlugin()
		{
			@Override
			public void create(StitchLink uplink)
			{
				log.debug("Test plugin create . . .");
				
				uplink.readRequest((s, r) ->
				{
					log.debug(r.toString());
					read = 1;
				});
			}
		};
		
		JSONObject manifest = new JSONObject();
		manifest.put("name", "TestPlugin");
		manifest.put("version", "1.0");
		manager.registerPlugin(testPlugin, manifest);
		
		Patch patch = AsyncPatch.create();
		manager.getPluginRouter().registerEndpoint("Endpoint", patch.getUpstream());
		patch.getDownstream().sendRequest(Request.create("TestPlugin", "HelloWorld", new JSONObject()));
		
		Thread.sleep(20);
		assertEquals("Request callback did not trigger", 1, read);
		patch.close();
	}
	
	@Test
	public void testAbstractPluginRouting() throws InterruptedException
	{
		PluginManager manager = PluginManager.create();
		
		CliffPlugin testPlugin = new AbstractCliffPlugin()
		{
			@Override
			public void registerRequests(RequestHandler requestHandler)
			{
				requestHandler.attachRequestCallback("HelloWorld", (s, r) ->
				{
					log.debug(r.toString());
					read++;
				});

				requestHandler.attachRequestCallback(null, (s, r) ->
				{
					log.debug(r.toString());
					read++;
				});
			}

			@Override
			public void start()
			{
				log.debug("Test plugin start . . .");
			}
		};
		
		JSONObject manifest = new JSONObject();
		manifest.put("name", "TestPlugin");
		manifest.put("version", "2.0");
		manager.registerPlugin(testPlugin, manifest);
		
		Patch patch = AsyncPatch.create();
		manager.getPluginRouter().registerEndpoint("Endpoint", patch.getUpstream());
		StitchLink downstream = patch.getDownstream();
		downstream.sendRequest(Request.create("TestPlugin", "HelloWorld", new JSONObject()));
		Thread.sleep(20);
		downstream.sendRequest(Request.create("TestPlugin", "NotHelloWorld", new JSONObject()));

		Thread.sleep(20);
		assertEquals("Request callback did not trigger", 2, read);
		patch.close();
	}
}
