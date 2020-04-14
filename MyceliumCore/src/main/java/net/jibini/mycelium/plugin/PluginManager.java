package net.jibini.mycelium.plugin;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.mycelium.file.TextFile;
import net.jibini.mycelium.routing.AsyncPatch;
import net.jibini.mycelium.routing.Patch;
import net.jibini.mycelium.routing.RequestRouter;

public class PluginManager
{
	private static Logger log = LoggerFactory.getLogger(PluginManager.class);
	
	private static class JarClassLoader extends URLClassLoader
	{
		public JarClassLoader(URL[] urls)
		{
			super(urls);
		}

		@Override
		public void addURL(URL url)
		{
			super.addURL(url);
		}
	}
	
	private JarClassLoader classLoader = new JarClassLoader(new URL[0]);
	
	private RequestRouter pluginRouter = RequestRouter.create("target");
	private Object pluginStartLock = new Object();
	
	
	private PluginManager()
	{}
	
	public static PluginManager create()
	{
		return new PluginManager();
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
		
		String pluginText = new TextFile().from(pluginStream).readRemaining(true);
		pluginStream.close();
		JSONObject manifest = new JSONObject(pluginText);
		
		return manifest;
	}
	
	private boolean checkDep(JSONObject manifest, Map<String, MyceliumSpore> loaded)
	{
		if (!manifest.has("dependencies"))
			return true;
		List<Object> depList = manifest.getJSONArray("dependencies").toList();
		String name = manifest.getString("name");
		
		for (Object o : depList)
			if (!loaded.containsKey((String)o))
			{
				log.error("Could not find dependency for '" + name + "': '" + (String)o + "'");
				return false;
			}
		
		return true;
	}
	
	private JSONObject loadPlugin(File file, Map<String, MyceliumSpore> loaded, boolean register)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
	{
		URLClassLoader jar = new URLClassLoader(new URL[] { file.toURI().toURL() });
		JSONObject plugin = getPluginManifest(jar);
		
		Class<?> pluginClass = Class.forName(plugin.getString("class"), true, classLoader);
		MyceliumSpore instance = (MyceliumSpore)pluginClass.newInstance();
		
		synchronized (loaded)
		{
			loaded.put(plugin.getString("name"), instance);
		}
		
		if (register)
			registerPlugin(instance, plugin);
		return plugin;
	}
	
	public void loadPlugins(File directory)
	{
		try
		{
			if (!directory.exists())
				directory.mkdirs();
			Map<String, MyceliumSpore> loaded = new HashMap<>();
			Map<String, JSONObject> man = new HashMap<>();
			
			File[] children = directory.listFiles(new FileFilter()
			{
				@Override
				public boolean accept(File file)
				{
					try
					{
						boolean acc = file.getName().endsWith(".jar");
						if (acc)
							classLoader.addURL(file.toURI().toURL());
						return acc;
					} catch (Throwable t)
					{
						log.error("Failed to add URL to classloader", t);
						return false;
					}
				}
			});
			
			for (File file : children)
				try
				{
					JSONObject m = loadPlugin(file, loaded, false);
					man.put(m.getString("name"), m);
				} catch (Throwable t)
				{
					log.error("Failed to load plugin file '" + file.getName() + "'", t);
				}
			
			for (String name : loaded.keySet())
				try
				{
					if (checkDep(man.get(name), loaded))
						registerPlugin(loaded.get(name), man.get(name));
				} catch (Throwable t)
				{
					log.error("Failed to check plugin dependencies for '" + name + "'", t);
				}
		} catch (Throwable t)
		{
			log.error("Failed to load plugin directory", t);
		}
	}
	
	public void registerPlugin(MyceliumSpore plugin, JSONObject manifest)
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
