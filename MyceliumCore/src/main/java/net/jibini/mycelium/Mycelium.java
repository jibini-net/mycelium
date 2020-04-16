package net.jibini.mycelium;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.mycelium.file.TextFile;
import net.jibini.mycelium.plugin.PluginManager;

public class Mycelium
{
	private static Mycelium INSTANCE = null;
	private static Object KILL_SWITCH = new Object();
	
	private Mycelium()
	{}
	
	public static Mycelium create()
	{
		Mycelium result = new Mycelium();
		INSTANCE = result;
		return result;
	}
	
	public static Mycelium instance()
	{
		if (INSTANCE == null)
			Mycelium.create();
		return INSTANCE;
	}
	
	public static void kill()
	{
		synchronized (KILL_SWITCH)
		{
			KILL_SWITCH.notify();
		}
	}
	
	public static void main(String[] args)
	{
		Thread myceliumThread = new Thread(() ->
		{
			Mycelium mycelium = Mycelium.instance();
			mycelium.start();
		});
		
		myceliumThread.setName("MyceliumThread");
		myceliumThread.start();
		
		synchronized (KILL_SWITCH)
		{
			try
			{
				KILL_SWITCH.wait();
			} catch (InterruptedException ex)
			{}
		}
		
		System.exit(0);
	}
	
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private PluginManager pluginManager = PluginManager.create();
	
	public JSONObject manifest()
	{
		try
		{
			InputStream manifestStream = getClass().getClassLoader().getResourceAsStream("mycelium.json");
			JSONObject manifest = new JSONObject(new TextFile().from(manifestStream).readRemaining(true));
			return manifest;
		} catch (IOException ex)
		{
			log.error("Failed to load application manifest", ex);
			return null;
		}
	}
	
	public void start()
	{
		JSONObject manifest = manifest();
		log.info("Starting " + manifest.getString("app-name") + " " + manifest.getString("version"));
		
		pluginManager.loadPlugins(new File("plugins"));
		pluginManager.notifyPluginStart();
	}
	
	public PluginManager getPluginManager()  { return pluginManager; }
}
