package net.jibini.cliff.routing;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.cliff.plugin.PluginRouter;

public class TestStitchLink
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private int read = 0;
	private Throwable thrown = null;
	
	@Test
	public void testAsyncTube() throws InterruptedException
	{
		StitchLink link = AsyncTube.create();
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 0 }")));
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 1 }")));
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 2 }")));
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 3 }")));
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 4 }")));
		
		RequestCallback callback = (l, r) ->
		{
			try
			{
				assertEquals(read++, r.getBody().getInt("value"));
			} catch (Throwable thrown)
			{
				this.thrown = thrown;
			}
		};
		
		link.readRequest(callback);
		link.readRequest(callback);
		link.readRequest(callback);
		link.readRequest(callback);
		link.readRequest(callback);

		Thread.sleep(20);
		if (thrown != null)
			throw new RuntimeException(thrown);
		link.close();
	}
	
	@Test
	public void testAsyncWaiting() throws InterruptedException
	{
		StitchLink link = AsyncTube.create();
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 0 }")));
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 1 }")));
		
		RequestCallback callback = (l, r) ->
		{
			try
			{
				assertEquals(read++, r.getBody().getInt("value"));
			} catch (Throwable thrown)
			{
				this.thrown = thrown;
			}
		};
		
		link.readRequest(callback);
		link.readRequest(callback);
		link.readRequest(callback);
		link.readRequest(callback);
		link.readRequest(callback);

		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 2 }")));
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 3 }")));
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 4 }")));

		Thread.sleep(20);
		if (thrown != null)
			throw new RuntimeException(thrown);
		link.close();
	}
	
	@Test
	public void testRouting() throws InterruptedException
	{
		PluginRouter router = PluginRouter.create();
		
		Patch hello = AsyncPatch.create();
		router.registerEndpoint("Hello", hello.getUpstream());
		Patch world = AsyncPatch.create();
		router.registerEndpoint("World", world.getUpstream());
		
		StitchLink helloDownstream = hello.getDownstream();
		StitchLink worldDownstream = hello.getDownstream();
		
		worldDownstream.sendRequest(Request.create("Hello", "World", new JSONObject()));
		helloDownstream.readRequest((s, r) ->
		{
			assertEquals("World", r.getHeader().getString("request"));
			log.debug(r.toString());
			read = 1;
		});
		
		Thread.sleep(20);
		assertEquals("Request callback did not trigger", read, 1);

		log.debug("Should log a RuntimeException:");
		worldDownstream.sendRequest(Request.create("NotHello", "World", new JSONObject()));
		Thread.sleep(20);
		
		hello.close();
		world.close();
	}
}
