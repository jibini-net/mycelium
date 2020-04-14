package net.jibini.mycelium.config;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public final class ArrayNode<ParentType extends ConfigNode<?, ?>> implements ConfigNode<Integer, ParentType>
{
	private JSONArray contents = new JSONArray();
	private ParentType parent;
	private boolean orphan = true;
	
	public ArrayNode<ParentType> from(JSONArray contents, ParentType parent)
	{
		this.contents = contents;
		this.parent = parent;
		this.orphan = false;
		return this;
	}
	
	@Override
	public MapNode<ArrayNode<ParentType>> pushNode(Integer index)
	{
		return new MapNode<ArrayNode<ParentType>>().from(contents.getJSONObject(index), this);
	}
	
	public MapNode<ArrayNode<ParentType>> putNode()
	{
		put(new JSONObject());
		return pushNode(contents.length() - 1);
	}
	
	@Override
	public ArrayNode<ArrayNode<ParentType>> pushArray(Integer index)
	{
		return new ArrayNode<ArrayNode<ParentType>>().from(contents.getJSONArray(index), this);
	}

	public ArrayNode<ArrayNode<ParentType>> putArray()
	{
		put(new JSONArray());
		return pushArray(contents.length() - 1);
	}
	
	@Override
	public ParentType popNode()
	{
		if (orphan)
			throw new RuntimeException("Configuration node is orphaned");
		return parent;
	}
	
	public List<Object> values()
	{
		return contents.toList();
	}

	@Override
	public ArrayNode<ParentType> value(Integer index, Object value)
	{
		contents.put(index, value);
		return this;
	}
	
	public ArrayNode<ParentType> put(Object value)
	{
		contents.put(value);
		return this;
	}

	@Override
	public Object value(Integer index) { return contents.get(index); }

	@Override
	public String valueString(Integer index) { return contents.getString(index); }
	
	@Override
	public boolean valueBoolean(Integer index) { return contents.getBoolean(index); }

	@Override
	public int valueInt(Integer index) { return contents.getInt(index); }

	@Override
	public float valueFloat(Integer index) { return contents.getFloat(index); }

	@Override
	public double valueDouble(Integer index) { return contents.getDouble(index); }
	
	
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
	public JSONObject valueJSONObject(Integer index) { return contents.getJSONObject(index); }

	@Override
	public JSONArray valueJSONArray(Integer index) { return contents.getJSONArray(index); }
}
