package net.jibini.cliff.network.session;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.cliff.network.session.SessionManager.Handler;
import net.jibini.cliff.plugin.PluginManager;
import net.jibini.cliff.routing.AsyncPatch;
import net.jibini.cliff.routing.Patch;
import net.jibini.cliff.routing.Request;
import net.jibini.cliff.routing.StitchLink;

public class TestSessionPlugin
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private int read = 0;
	
	@Test
	public void testEventAnnotations() throws InterruptedException
	{
		PluginManager manager = PluginManager.create();
		
		SessionPlugin plugin = new SessionPlugin()
		{
			@Override
			public void start()
			{
				
			}
			
			@Handler("Request")
			public void onRequest(Request request)
			{
				read++;
				log.debug(request.toString());
			}
			
			@Handler("Responder")
			public boolean onResponder(Request request)
			{
				read++;
				log.debug(request.toString());
				
				return false;
			}
		};
		
		JSONObject manifest = new JSONObject();
		manifest.put("name", "TestSessionPlugin");
		manifest.put("version", "1.0");
		manager.registerPlugin(plugin, manifest);
		manager.notifyPluginStart();
		Thread.sleep(4);
		
		Patch patch = AsyncPatch.create();
		manager.getPluginRouter().registerEndpoint("Endpoint", patch.getUpstream());
		StitchLink downstream = patch.getDownstream();
		downstream.sendRequest(Request.create("TestSessionPlugin", "Request", new JSONObject()));
		downstream.sendRequest(Request.create("TestSessionPlugin", "Responder", new JSONObject()));

		Thread.sleep(20);
		assertEquals("Request callback did not trigger", 2, read);
		patch.close();
	}
	
	@Test
	public void testSessionParam() throws InterruptedException
	{
		PluginManager manager = PluginManager.create();
		
		SessionPlugin plugin = new SessionPlugin()
		{
			@Override
			public void start()
			{
				
			}
			
			@Handler("Request")
			public void onRequest(Request request, Session session)
			{
				log.debug(request.toString());
				log.debug("Session UUID: " + session.getSessionUUID().toString());
				log.debug("Session token: " + session.getToken());
				read++;
			}
		};
		
		JSONObject manifest = new JSONObject();
		manifest.put("name", "TestSessionPlugin");
		manifest.put("version", "2.0");
		manager.registerPlugin(plugin, manifest);
		manager.notifyPluginStart();
		Thread.sleep(4);
		
		Patch patch = AsyncPatch.create();
		manager.getPluginRouter().registerEndpoint("Endpoint", patch.getUpstream());
		StitchLink downstream = patch.getDownstream();
		
		Request createSession = Request.create("TestSessionPlugin", "CreateSession");
		createSession.getHeader().put("session", UUID.randomUUID().toString());
		downstream.sendRequest(createSession);
		
		downstream.readRequest((s, r) ->
		{
			Session session = Session.create(r.getHeader().getString("session"), r.getResponse().getString("token"));
			Request req = Request.create("TestSessionPlugin", "Request");
			session.embed(req);
			s.sendRequest(req);
		});

		Thread.sleep(20);
		assertEquals("Request callback did not trigger", 1, read);
		patch.close();
	}
}
