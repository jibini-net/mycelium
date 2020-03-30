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
	private static Logger log = LoggerFactory.getLogger(TestSessionPlugin.class);
	
	private static int read = 0;
	
	private static Object lock = new Object();
	
	private static void w() throws InterruptedException
	{
		if (lock != null)
			synchronized (lock)
			{
				if (lock != null)
					lock.wait();
			}
	}
	
	private static void n()
	{
		synchronized (lock)
		{
			lock.notifyAll();
			lock = null;
		}
	}
	
	public static class TestEventAnnotationsKernel extends SessionKernel
	{
		public TestEventAnnotationsKernel(Session parent)
		{
			super(parent);
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
			n();
			
			return false;
		}
	}
	
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

			@Override
			public Class<? extends SessionKernel> getKernelClass()
			{
				return TestEventAnnotationsKernel.class;
			}
		};
		
		JSONObject manifest = new JSONObject();
		manifest.put("name", "TestSessionPlugin");
		manifest.put("version", "1.0");
		manager.registerPlugin(plugin, manifest);
		manager.notifyPluginStart();
		Thread.sleep(100);
		
		Patch patch = AsyncPatch.create();
		manager.getPluginRouter().registerEndpoint("Endpoint", patch.getUpstream());
		StitchLink downstream = patch.getDownstream();
		
		Request createSession = Request.create("TestSessionPlugin", "CreateSession");
		createSession.getHeader().put("session", UUID.randomUUID().toString());
		downstream.sendRequest(createSession);
		
		downstream.readRequest((s, r) ->
		{
			Session session = Session.create(null, r.getHeader().getString("session"), r.getResponse().getString("token"));
			
			Request req0 = Request.create("TestSessionPlugin", "Request", new JSONObject());
			session.embed(req0);
			s.sendRequest(req0);
			
			try
			{
				Thread.sleep(100);
			} catch (InterruptedException ex)
			{}
			
			Request req1 = Request.create("TestSessionPlugin", "Responder", new JSONObject());
			session.embed(req1);
			s.sendRequest(req1);
		});

		w();
		assertEquals("Request callback did not trigger", 2, read);
		patch.close();
		read = 0;
		lock = new Object();
	}
	
	public static class TestSessionParamKernel extends SessionKernel
	{
		public TestSessionParamKernel(Session parent)
		{
			super(parent);
		}

		@Handler("Request")
		public void onRequest(Request request, Session session)
		{
			log.debug(request.toString());
			log.debug("Session UUID: " + session.getSessionUUID().toString());
			log.debug("Session token: " + session.getToken());
			read++;
			n();
		}
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

			@Override
			public Class<? extends SessionKernel> getKernelClass()
			{
				return TestSessionParamKernel.class;
			}
		};
		
		JSONObject manifest = new JSONObject();
		manifest.put("name", "TestSessionPlugin");
		manifest.put("version", "2.0");
		manager.registerPlugin(plugin, manifest);
		manager.notifyPluginStart();
		Thread.sleep(20);
		
		Patch patch = AsyncPatch.create();
		manager.getPluginRouter().registerEndpoint("Endpoint", patch.getUpstream());
		StitchLink downstream = patch.getDownstream();
		
		Request createSession = Request.create("TestSessionPlugin", "CreateSession");
		createSession.getHeader().put("session", UUID.randomUUID().toString());
		downstream.sendRequest(createSession);
		
		downstream.readRequest((s, r) ->
		{
			Session session = Session.create(null, r.getHeader().getString("session"), r.getResponse().getString("token"));
			Request req = Request.create("TestSessionPlugin", "Request");
			session.embed(req);
			s.sendRequest(req);
		});

		w();
		assertEquals("Request callback did not trigger", 1, read);
		patch.close();
		read = 0;
		lock = new Object();
	}
}
