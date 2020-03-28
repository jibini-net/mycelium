package net.jibini.cliff.routing;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.cliff.api.ResponderCallback;

public class TestRouting
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private int read = 0;
	
	@Test
	public void testRouting() throws InterruptedException
	{
		RequestRouter router = RequestRouter.create("target");
		
		Patch hello = AsyncPatch.create();
		router.registerEndpoint("Hello", hello.getUpstream());
		Patch world = AsyncPatch.create();
		router.registerEndpoint("World", world.getUpstream());
		
		StitchLink helloDownstream = hello.getDownstream();
		StitchLink worldDownstream = world.getDownstream();
		
		worldDownstream.sendRequest(Request.create("Hello", "World", new JSONObject()));
		helloDownstream.readRequest((s, r) ->
		{
			assertEquals("World", r.getHeader().getString("request"));
			log.debug(r.toString());
			read = 1;
		});
		
		Thread.sleep(20);
		assertEquals("Request callback did not trigger", 1, read);

		log.debug("Should log a RuntimeException:");
		worldDownstream.sendRequest(Request.create("NotHello", "World", new JSONObject()));
		Thread.sleep(20);
		
		hello.close();
		world.close();
	}
	
	@Test
	public void testMultiLevelRouting() throws InterruptedException
	{
		RequestRouter routeA = RequestRouter.create("target", true);
		RequestRouter routeB = RequestRouter.create("target", true);
		RequestRouter routeC = RequestRouter.create("target", true);
		RequestRouter routeD = RequestRouter.create("target", true);
		
		Patch plugA = AsyncPatch.create();
		routeA.registerEndpoint("PlugA", plugA.getUpstream());
		
		plugA.getDownstream().addPersistentCallback((s, r) ->
		{
			read = r.getResponse().getInt("value");
			log.debug(r.toString());
		});

		Patch plugD = AsyncPatch.create();
		routeD.registerEndpoint("PlugD", plugD.getUpstream());
		
		plugD.getDownstream().addPersistentCallback(ResponderCallback.create((s, r) ->
		{
			log.debug(r.toString());
			r.getResponse().put("value", 1337);
			
			return true;
		}));
		
		Patch aToB = AsyncPatch.create();
		routeA.registerEndpoint(RequestRouter.UPSTREAM_NAME, aToB.getUpstream());
		routeB.registerEndpoint("A", aToB.getDownstream());
		
		Patch bToC = AsyncPatch.create();
		routeB.registerEndpoint(RequestRouter.UPSTREAM_NAME, bToC.getUpstream());
		routeC.registerEndpoint("B", bToC.getDownstream());
		
		Patch cToD = AsyncPatch.create();
		routeC.registerEndpoint(RequestRouter.UPSTREAM_NAME, cToD.getUpstream());
		routeD.registerEndpoint("C", cToD.getDownstream());
		
		Thread.sleep(4);
		Request request = Request.create("PlugD", "TestRequest");
		plugA.getDownstream().sendRequest(request);
		Thread.sleep(20);
		assertEquals("Request callback did not trigger", 1337, read);
		
		aToB.close();
		bToC.close();
		cToD.close();
	}
	
	@Test
	public void testMultiLabelRouting() throws InterruptedException
	{
		RequestRouter routeA = RequestRouter.create("a", false);
		RequestRouter routeB = RequestRouter.create("b", false);
		RequestRouter routeC = RequestRouter.create("c", false);
		RequestRouter routeD = RequestRouter.create("d", false);
		
		Patch plugA = AsyncPatch.create();
		routeA.registerEndpoint("PlugA", plugA.getUpstream());
		
		plugA.getDownstream().addPersistentCallback((s, r) ->
		{
			read = r.getResponse().getInt("value");
			log.debug(r.toString());
		});

		Patch plugD = AsyncPatch.create();
		routeD.registerEndpoint("PlugD", plugD.getUpstream());
		
		plugD.getDownstream().addPersistentCallback(ResponderCallback.create((s, r) ->
		{
			log.debug(r.toString());
			r.getResponse().put("value", 1337);
			
			return true;
		}));
		
		Patch aToB = AsyncPatch.create();
		routeA.registerEndpoint("B", aToB.getUpstream());
		routeB.registerEndpoint("A", aToB.getDownstream());
		
		Patch bToC = AsyncPatch.create();
		routeB.registerEndpoint("C", bToC.getUpstream());
		routeC.registerEndpoint("B", bToC.getDownstream());
		
		Patch cToD = AsyncPatch.create();
		routeC.registerEndpoint("D", cToD.getUpstream());
		routeD.registerEndpoint("C", cToD.getDownstream());
		
		Thread.sleep(4);
		Request request = Request.create("PlugD", "TestRequest");
		request.getHeader().put("a", "B");
		request.getHeader().put("b", "C");
		request.getHeader().put("c", "D");
		request.getHeader().put("d", "PlugD");
		plugA.getDownstream().sendRequest(request);
		Thread.sleep(20);
		assertEquals("Request callback did not trigger", 1337, read);
		
		aToB.close();
		bToC.close();
		cToD.close();
	}
}
