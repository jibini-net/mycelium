package net.jibini.cliff.plugin;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		assertEquals("Request callback did not trigger", read, 1);
		patch.close();
	}
}
