package net.jibini.mycelium.conf;

import org.json.JSONArray;
import org.json.JSONObject;

public class MapConfigNode implements ConfigNode<String>
{
	private final JSONObject origin;
	
	public MapConfigNode(JSONObject origin)
	{ this.origin = origin; }
	
	
	@Override
	public MapConfigNode value(String key, Object value)
	{ origin.put(key, value); return this; }
	
	public MapConfigNode defaultValue(String key, Object value)
	{ if (!origin.has(key)) origin.put(key, value); return this; }
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T value(String key)
	{ return (T)origin.get(key); }
	
	
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
