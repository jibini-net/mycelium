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
	
	public static JSONObject getPluginManifest(URLClassLoader plugin) throws IOException
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
	
	private PluginRouter pluginRouter = PluginRouter.create();
	
	public static PluginManager create()
	{
		return new PluginManager();
	}
	
	public void registerPlugin(CliffPlugin plugin, JSONObject manifest)
	{
		Patch patch = AsyncPatch.create();
		try
		{
			log.info("Loading plugin '" + manifest.getString("name") + "' (" + manifest.getString("version")
					+ ") . . .");
			
			plugin.create(patch.getDownstream());
			getPluginRouter().registerEndpoint(manifest.getString("name"), patch.getUpstream());
			log.debug("Registered patch '" + manifest.getString("name") + "'");
		} catch (Throwable t)
		{
			log.error("Failed to create plugin", t);
		}
	}
	
	public PluginRouter getPluginRouter()  { return pluginRouter; }
}
