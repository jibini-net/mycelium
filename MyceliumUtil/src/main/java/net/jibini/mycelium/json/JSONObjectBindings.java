package net.jibini.mycelium.json;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import net.jibini.mycelium.error.InvalidOperationException;
import net.jibini.mycelium.map.KeyValuePair;

public class JSONObjectBindings implements JSONBindings<String>
{
	private JSONObject origin = new JSONObject();
	
	public JSONObjectBindings from(JSONObject origin)
	{
		this.origin = origin;
		return this;
	}
	
	@Override
	public KeyValuePair<String, Object> keyValue(String key)
	{
		return new KeyValuePair<String, Object>()
				.withKey(key)
				.withValue(value(key));
	}

	@Override
	public Object value(String key) { return origin.get(key); }
	

	@Override
	public JSONObjectBindings insert(KeyValuePair<String, Object> keyValue)
	{
		origin.put(keyValue.key(), keyValue.value());
		return this;
	}

	@Override
	public JSONObjectBindings insert(String key, Object value)
	{
		origin.put(key, value);
		return this;
	}

	@Override
	public JSONObjectBindings append(Object value)
	{
		throw new InvalidOperationException("Cannot append value to JSONObject");
	}

	@Override
	public int size() { return origin.length(); }
	

	@Override
	public boolean hasKey(String key) { return origin.has(key); }

	@Override
	public boolean hasValue(Object value) { return origin.toMap().containsValue(value); }
	

	@Override
	public Iterable<KeyValuePair<String, Object>> iterable()
	{
		return new Iterable<KeyValuePair<String, Object>>()
				{
					@Override
					public Iterator<KeyValuePair<String, Object>> iterator()
					{
						return new Iterator<KeyValuePair<String, Object>>()
								{
									Iterator<String> keys = origin.keys();
							
									@Override
									public boolean hasNext()
									{
										return keys.hasNext();
									}

									@Override
									public KeyValuePair<String, Object> next()
									{
										String key = keys.next();
										return new KeyValuePair<String, Object>()
												.withKey(key)
												.withValue(value(key));
									}
								};
					}
				};
	}

	@Override
	public Iterable<Object> values() { return origin.toMap().values(); }

	@Override
	public String valueString(String key) { return origin.getString(key); }

	@Override
	public boolean valueBoolean(String key) { return origin.getBoolean(key); }

	@Override
	public int valueInt(String key) { return origin.getInt(key); }

	@Override
	public float valueFloat(String key) { return origin.getFloat(key); }

	@Override
	public double valueDouble(String key) { return origin.getDouble(key); }

	@Override
	public JSONObject valueJSONObject(String key) { return origin.getJSONObject(key); }

	@Override
	public JSONArray valueJSONArray(String key) { return origin.getJSONArray(key); }
	
	@Override
	public String toString() { return origin.toString(); }
	
	@Override
	public String toString(int indentFactor) { return origin.toString(indentFactor); }
}
