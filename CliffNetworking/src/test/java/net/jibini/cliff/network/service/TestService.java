package net.jibini.cliff.network.service;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.cliff.network.session.Session;
import net.jibini.cliff.network.session.SessionKernel;
import net.jibini.cliff.network.session.SessionManager.Handler;
import net.jibini.cliff.network.session.SessionPlugin;
import net.jibini.cliff.plugin.PluginManager;
import net.jibini.cliff.routing.AsyncPatch;
import net.jibini.cliff.routing.Patch;
import net.jibini.cliff.routing.Request;
import net.jibini.cliff.routing.StitchLink;

public class TestService
{
	private static Logger log = LoggerFactory.getLogger(TestService.class);
	
	private static int read = 0;
	
	private PluginManager manager = PluginManager.create();
	private StitchLink downstream;
	
	public static class TestPluginKernel extends SessionKernel
	{
		public TestPluginKernel(Session parent)
		{
			super(parent);
			log.debug("Plugin kernel create . . .");
		}
		
		@Handler("TestRequest")
		public boolean onRequest(Request request)
		{
			log.debug("Request received");
			request.getResponse().put("Hello", "World");
			read++;
			
			return true;
		}
	}
	
	@Before
	public void startPlugin() throws InterruptedException
	{
		read = 0;
		
		SessionPlugin plugin = new SessionPlugin()
		{
			@Override
			public Class<? extends SessionKernel> getKernelClass()
			{
				return TestPluginKernel.class;
			}

			@Override
			public void start()
			{
				log.debug("Plugin start . . .");
			}
		};
		
		JSONObject manifest = new JSONObject();
		manifest.put("name", "Plugin");
		manifest.put("version", "1.0");
		manager.registerPlugin(plugin, manifest);
		manager.notifyPluginStart();
		Thread.sleep(4);
		
		Patch patch = AsyncPatch.create();
		manager.getPluginRouter().registerEndpoint("Endpoint", patch.getUpstream());
		downstream = patch.getDownstream();
	}
	
	public static class TestServiceKernel extends SessionKernel
	{
		public TestServiceKernel(Session parent)
		{
			super(parent);
			log.debug("Service kernel create . . .");
		}
		
		@Handler("TestRequest")
		public void onResponse()
		{
			log.debug("Response received");
			read++;
		}
	}
	
	@Test
	public void testPluginService() throws InterruptedException
	{
		downstream.addPersistentCallback((s, r) ->
		{
			log.debug(r.toString());
		});
		
		Service service = Service.create(downstream, "Plugin", TestServiceKernel.class);
		service.waitSessionCreation();
		service.sendRequest(Request.create("Plugin", "TestRequest"));
		
		Thread.sleep(20);
		assertEquals("Request callback did not trigger", 2, read);
		read = 0;
	}
}
