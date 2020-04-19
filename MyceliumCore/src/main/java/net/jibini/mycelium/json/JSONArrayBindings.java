package net.jibini.mycelium.json;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import net.jibini.mycelium.map.KeyValuePair;

public final class JSONArrayBindings implements JSONBindings<Integer>
{
	private JSONArray origin = new JSONArray();
	
	public JSONArrayBindings from(JSONArray origin)
	{
		this.origin = origin;
		return this;
	}
	
	@Override
	public KeyValuePair<Integer, Object> keyValue(Integer key)
	{
		return new KeyValuePair<Integer, Object>()
				.withKey(key)
				.withValue(value(key));
	}

	@Override
	public Object value(Integer key) { return origin.get(key); }
	

	@Override
	public JSONArrayBindings insert(KeyValuePair<Integer, Object> keyValue)
	{
		origin.put(keyValue.key(), keyValue.value());
		return this;
	}

	/*
	 * Expected behavior:
	 * JSONArrayBindings mimics the insertion behavior of JSONArray,
	 * therefore "inserting" an object at an existing index replaces
	 * the previous value.
	 */
	@Override
	public JSONArrayBindings insert(Integer key, Object value)
	{
		origin.put(key, value);
		return this;
	}

	@Override
	public JSONArrayBindings append(Object value)
	{
		origin.put(value);
		return this;
	}

	@Override
	public int size() { return origin.length(); }
	

	@Override
	public boolean hasKey(Integer key) { return size() > key; }

	@Override
	public boolean hasValue(Object value) { return origin.toList().contains(value); }
	

	@Override
	public Iterable<KeyValuePair<Integer, Object>> iterable()
	{
		return new Iterable<KeyValuePair<Integer, Object>>()
				{
					@Override
					public Iterator<KeyValuePair<Integer, Object>> iterator()
					{
						return new Iterator<KeyValuePair<Integer, Object>>()
								{
									int i = 0;
							
									@Override
									public boolean hasNext()
									{
										return hasKey(i);
									}

									@Override
									public KeyValuePair<Integer, Object> next()
									{
										return keyValue(i ++);
									}
								};
					}
				};
	}

	@Override
	public Iterable<Object> values() { return origin; }

	@Override
	public String valueString(Integer key) { return origin.getString(key); }

	@Override
	public boolean valueBoolean(Integer key) { return origin.getBoolean(key); }

	@Override
	public int valueInt(Integer key) { return origin.getInt(key); }

	@Override
	public float valueFloat(Integer key) { return origin.getFloat(key); }

	@Override
	public double valueDouble(Integer key) { return origin.getDouble(key); }

	@Override
	public JSONObject valueJSONObject(Integer key) { return origin.getJSONObject(key); }

	@Override
	public JSONArray valueJSONArray(Integer key) { return origin.getJSONArray(key); }
	
	@Override
	public String toString() { return origin.toString(); }
	
	@Override
	public String toString(int indentFactor) { return origin.toString(indentFactor); }
}
