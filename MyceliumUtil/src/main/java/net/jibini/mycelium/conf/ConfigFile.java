package net.jibini.mycelium.conf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.jibini.mycelium.file.TextFile;
import net.jibini.mycelium.link.Closeable;
import net.jibini.mycelium.resource.Checked;

public class ConfigFile implements Closeable, ConfigNode<String>
{
	private TextFile origin = new TextFile();
	private Checked<JSONObject> data = new Checked<JSONObject>()
			.withName("Config Data Map");
	
	private boolean cached = false;
	
	public ConfigFile from(File file)
	{ origin.from(file); return this; }

	public ConfigFile from(InputStream in, OutputStream out)
	{ origin.from(in, out); return this; }

	public ConfigFile from(InputStream in)
	{ origin.from(in); return this; }
	
	public ConfigFile to(OutputStream out)
	{ origin.to(out); return this; }
	
	public ConfigFile at(String path)
	{ origin.at(path); return this; }

	public ConfigFile from(JSONObject data)
	{ this.data.value(data); return this; }
	
	@Override
	public ConfigFile value(String key, Object value)
	{ data.value().put(key, value); return this; }
	
	public ConfigFile defaultValue(String key, Object value)
	{ if (!data.value().has(key)) data.value().put(key, value); return this; }
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T value(String key)
	{ return (T)data.value().get(key); }
	
	
	public ConfigFile createIfNotExist() throws IOException
	{ origin.createIfNotExist("{ }"); return this; }
	
	public ConfigFile load() throws IOException
	{
		createIfNotExist();
		data.value(new JSONObject(origin.readRemaining()));
		cached = true;
		return this;
	}
	
	public ConfigFile write() throws IOException
	{ origin.overwrite(data.value().toString(4)); return this; }
	
	public ConfigFile delete()
	{ origin.delete(); return this; }
	
	public ConfigFile deleteOnExit()
	{ origin.deleteOnExit(); return this; }
	
	@Override
	public ConfigFile close()
	{ origin.close(); return this; }
	
	
	public boolean isCached()
	{ return cached; }

	@Override
	public boolean isAlive()
	{ return origin.isAlive(); }
	
	@Override
	public String toString()
	{ return data.value().toString(); }
	
	public String toString(int indentFactor)
	{ return data.value().toString(indentFactor); }
	
	
	@Override
	public MapConfigNode map(String key)
	{
		return new MapConfigNode(this.defaultValue(key, new JSONObject())
			.<JSONObject>value(key));
	}

	@Override
	public ArrayConfigNode array(String key)
	{
		return new ArrayConfigNode(this.defaultValue(key, new JSONArray())
				.<JSONArray>value(key));
	}
}
