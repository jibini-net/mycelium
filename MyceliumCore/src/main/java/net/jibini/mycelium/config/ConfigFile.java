package net.jibini.mycelium.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.jibini.mycelium.file.TextFile;

public final class ConfigFile implements ConfigNode<String, ConfigFile>
{
	private final TextFile origin = new TextFile();
	
	private MapNode<ConfigFile> contents = new MapNode<ConfigFile>().from(new JSONObject(), this);
	
	public ConfigFile from(InputStream in, OutputStream out)
	{
		origin.from(in, out);
		return this;
	}
	
	public ConfigFile from(InputStream in) { return from(in, null); }
	
	public ConfigFile from(OutputStream out) { return from(null, out); }
	
	
	public ConfigFile from(File file) throws FileNotFoundException
	{
		origin.from(file);
		return this;
	}
	
	public ConfigFile at(String path) throws FileNotFoundException { return from(new File(path)); }
	
	
	public ConfigFile createIfNotExist() throws IOException
	{
		origin.createIfNotExist("{ }");
		return this;
	}

	public ConfigFile load() throws IOException
	{
		createIfNotExist();
		contents = new MapNode<ConfigFile>().from(new JSONObject(origin.readRemaining()), this);
		return this;
	}

	public ConfigFile write() throws IOException
	{
		origin.overwrite(toString(4));
		return this;
	}
	
	public ConfigFile defaultValue(String name, Object value)
	{
		contents.defaultValue(name, value);
		return this;
	}

	@Override
	public ConfigFile value(String name, Object value)
	{
		contents.value(name, value);
		return this;
	}

	@Override
	public Object value(String name) { return contents.value(name); }

	@Override
	public String valueString(String name) { return contents.valueString(name); }
	
	@Override
	public boolean valueBoolean(String name) { return contents.valueBoolean(name); }

	@Override
	public int valueInt(String name) { return contents.valueInt(name); }

	@Override
	public float valueFloat(String name) { return contents.valueFloat(name); }

	@Override
	public double valueDouble(String name) { return contents.valueDouble(name); }
	
	
	public ConfigFile delete()
	{
		origin.delete();
		return this;
	}

	public ConfigFile deleteOnExit()
	{
		origin.deleteOnExit();
		return this;
	}
	
	public String toString(int indentFactor) { return contents.toString(indentFactor); }

	public String toString() { return contents.toString(); }
	
	public MapNode<MapNode<ConfigFile>> pushNode(String name) { return contents.pushNode(name); }

	@Override
	public boolean isOrphan() { return true; }

	@Override
	public JSONObject valueJSONObject(String name) { return contents.valueJSONObject(name); }

	@Override
	public JSONArray valueJSONArray(String name) { return contents.valueJSONArray(name); }

	@Override
	public ArrayNode<MapNode<ConfigFile>> pushArray(String key)
	{
		return contents.pushArray(key);
	}
	
	public ArrayNode<MapNode<ConfigFile>> defaultArray(String key) { return contents.defaultArray(key); }
	

	@Override
	public ConfigFile popNode()
	{
		if (isOrphan())
			throw new RuntimeException("Configuration node is orphaned");
		return this;
	}
	
	public ConfigFile close() throws IOException 
	{
		origin.close();
		return this;
	}
}
