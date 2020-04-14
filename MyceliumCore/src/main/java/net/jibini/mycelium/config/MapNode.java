package net.jibini.mycelium.config;

import org.json.JSONArray;
import org.json.JSONObject;

public final class MapNode<ParentType extends ConfigNode<?, ?>> implements ConfigNode<String, ParentType>
{
	private JSONObject contents = new JSONObject();
	private ParentType parent;
	
	private boolean orphan = true;
	
	public MapNode<ParentType> from(JSONObject contents)
	{
		this.contents = contents;
		return this;
	}
	
	public MapNode<ParentType> from(JSONObject contents, ParentType parent)
	{
		this.parent = parent;
		this.orphan = false;
		return from(contents);
	}
	
	@Override
	public MapNode<MapNode<ParentType>> pushNode(String name)
	{
		defaultValue(name, new JSONObject());
		return new MapNode<MapNode<ParentType>>().from(contents.getJSONObject(name), this);
	}
	
	@Override
	public ParentType popNode()
	{
		if (orphan)
			throw new RuntimeException("Configuration node is orphaned");
		return parent;
	}

	public MapNode<ParentType> defaultValue(JSONObject node, String key, Object value)
	{
		if (!node.has(key))
			node.put(key, value);
		return this;
	}

	public MapNode<ParentType> defaultValue(String key, Object value)
	{
		return defaultValue(contents, key, value);
	}
	
	@Override
	public ArrayNode<MapNode<ParentType>> pushArray(String name)
	{
		defaultValue(name, new JSONArray());
		return new ArrayNode<MapNode<ParentType>>().from(contents.getJSONArray(name), this);
	}
	
	public ArrayNode<MapNode<ParentType>> defaultArray(String name)
	{
		if (contents.has(name))
			return new ArrayNode<MapNode<ParentType>>().from(new JSONArray(), this);
		else
		{
			defaultValue(name, new JSONArray());
			return new ArrayNode<MapNode<ParentType>>().from(contents.getJSONArray(name), this);
		}
	}

	@Override
	public MapNode<ParentType> value(String key, Object value)
	{
		contents.put(key, value);
		return this;
	}

	@Override
	public Object value(String name) { return contents.get(name); }

	@Override
	public String valueString(String name) { return contents.getString(name); }
	
	@Override
	public boolean valueBoolean(String name) { return contents.getBoolean(name); }

	@Override
	public int valueInt(String name) { return contents.getInt(name); }

	@Override
	public float valueFloat(String name) { return contents.getFloat(name); }

	@Override
	public double valueDouble(String name) { return contents.getDouble(name); }
	

	public String toString(int indentFactor)
	{
		return contents.toString(indentFactor);
	}

	@Override
	public String toString()
	{
		return contents.toString();
	}

	@Override
	public boolean isOrphan() { return orphan; }

	@Override
	public JSONObject valueJSONObject(String name) { return contents.getJSONObject(name); }

	@Override
	public JSONArray valueJSONArray(String name) { return contents.getJSONArray(name); }
}
