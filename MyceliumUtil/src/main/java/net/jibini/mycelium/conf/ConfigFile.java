package net.jibini.mycelium.conf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONObject;

import net.jibini.mycelium.file.TextFile;
import net.jibini.mycelium.json.JSONObjectBindings;
import net.jibini.mycelium.link.Closeable;

public class ConfigFile extends AbstractConfigNode<String, ConfigFile, ConfigFile>
		implements Closeable
{
	private JSONObjectBindings dataMap = new JSONObjectBindings();
	private TextFile origin = new TextFile();
	
	private boolean cached = false;

	public ConfigFile from(InputStream in, OutputStream out) { origin.from(in, out); return this; }

	public ConfigFile from(InputStream in) { origin.from(in); return this; }
	
	public ConfigFile from(OutputStream out) { origin.from(out); return this; }
	
	public ConfigFile from(File file) { origin.from(file); return this; }
	
	public ConfigFile at(String path) { origin.at(path); return this; }
	
	
	public ConfigFile createIfNotExist() throws IOException
	{
		origin.createIfNotExist("{ }");
		return this;
	}
	
	public ConfigFile load() throws IOException
	{
		createIfNotExist();
		dataMap.from(new JSONObject(origin.readRemaining()));
		cached = true;
		return this;
	}
	
	public ConfigFile write() throws IOException
	{
		origin.overwrite(toString(4));
		return this;
	}
	
	public ConfigFile delete() { origin.delete(); return this; }
	
	public ConfigFile deleteOnExit() { origin.deleteOnExit(); return this; }
	
	@Override
	public ConfigFile close() { origin.close(); return this; }
	
	
	@Override
	public ConfigFile pop() { return this; }

	@Override
	public JSONObjectBindings dataMap() { return dataMap; }
	
	public boolean isCached() { return cached; }

	@Override
	public boolean isAlive() { return origin.isAlive(); }
}
