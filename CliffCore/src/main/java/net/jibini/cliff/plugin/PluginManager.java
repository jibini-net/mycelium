package net.jibini.cliff.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.cliff.routing.AsyncPatch;
import net.jibini.cliff.routing.Patch;
import net.jibini.cliff.routing.RequestRouter;
import net.jibini.cliff.util.StreamUtil;

public class PluginManager
{
	private static Logger log = LoggerFactory.getLogger(PluginManager.class);
	
	public static CliffPlugin loadPlugin(File file, JSONObject data)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
	{
		URLClassLoader jar = new URLClassLoader(new URL[] { file.toURI().toURL() });
		JSONObject plugin = getPluginManifest(jar);
		Class<?> pluginClass = Class.forName(plugin.getString("class"), true, jar);
		CliffPlugin instance = (CliffPlugin)pluginClass.newInstance();
		return instance;
	}
	
	public static JSONObject getPluginManifest(ClassLoader plugin) throws IOException
	{
		InputStream pluginStream = plugin.getResourceAsStream("plugin.json");
		
		if (pluginStream == null)
		{
			if (plugin instanceof URLClassLoader)
				((URLClassLoader)plugin).close();
			throw new RuntimeException("Plugin failed to load, missing 'plugin.json'");
		}
		
		String pluginText = StreamUtil.readTextFile(pluginStream);
		JSONObject manifest = new JSONObject(pluginText);
		
		return manifest;
	}
	
	
	private PluginManager()
	{}
	
	private RequestRouter pluginRouter = RequestRouter.create("target");
	private Object pluginStartLock = new Object();
	
	public static PluginManager create()
	{
		return new PluginManager();
	}
	
	public void registerPlugin(CliffPlugin plugin, JSONObject manifest)
	{
		try
		{
			Patch patch = AsyncPatch.create();
			log.info("Loading plugin '" + manifest.getString("name") + "' (" + manifest.getString("version")
					+ ") . . .");
			
			Thread pluginThread = new Thread(() -> plugin.create(this, manifest, patch.getDownstream()));
			pluginThread.setName(manifest.getString("name"));
			pluginThread.start();
			
			getPluginRouter().registerEndpoint(manifest.getString("name"), patch.getUpstream());
			log.debug("Registered patch '" + manifest.getString("name") + "'");
		} catch (Throwable t)
		{
			log.error("Failed to create plugin", t);
		}
	}
	
	public RequestRouter getPluginRouter()  { return pluginRouter; }
	
	public void waitPluginStart()
	{
		if (pluginStartLock == null)
			log.debug("Plugin start lock already notified");
		else
			synchronized (pluginStartLock)
			{
				if (pluginStartLock != null)
					try
					{
						log.debug("Waiting for plugin start lock");
						pluginStartLock.wait();
					} catch (InterruptedException ex)
					{
						log.error("Plugin start lock interrupted", ex);
					}
			}
				
	}
	
	public void notifyPluginStart()
	{
		if (pluginStartLock != null)
			synchronized (pluginStartLock)
			{
				log.info("Notifying plugin start lock");
				pluginStartLock.notifyAll();
			}
		
		pluginStartLock = null;
	}
}
