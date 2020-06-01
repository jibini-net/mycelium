package net.jibini.mycelium.conf;

import org.json.JSONArray;
import org.json.JSONObject;

public class ArrayConfigNode implements ConfigNode<Integer>
{
	private final JSONArray origin;
	
	public ArrayConfigNode(JSONArray origin)
	{ this.origin = origin; }
	
	
	@Override
	public ArrayConfigNode value(Integer key, Object value)
	{ origin.put(key, value); return this; }
	
	public ArrayConfigNode append(Object value)
	{ origin.put(value); return this; }
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T value(Integer key)
	{ return (T)origin.get(key); }
	
	
	@Override
	public MapConfigNode map(Integer key)
	{ return new MapConfigNode(this.<JSONObject>value(key)); }
	

	public MapConfigNode appendMap()
	{
		JSONObject newMap = new JSONObject();
		append(newMap);
		return new MapConfigNode(newMap);
	}

	@Override
	public ArrayConfigNode array(Integer key)
	{ return new ArrayConfigNode(this.<JSONArray>value(key)); }
	
	
	public ArrayConfigNode appendArray()
	{
		JSONArray newArray = new JSONArray();
		append(newArray);
		return new ArrayConfigNode(newArray);
	}
}
