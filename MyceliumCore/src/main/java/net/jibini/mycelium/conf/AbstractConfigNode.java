package net.jibini.mycelium.conf;

import org.json.JSONArray;
import org.json.JSONObject;

import net.jibini.mycelium.json.JSONArrayBindings;
import net.jibini.mycelium.json.JSONObjectBindings;

public abstract class AbstractConfigNode<K, P, S extends ConfigNode<K, P>> 
		implements ConfigNode<K, P>
{
	@SuppressWarnings("unchecked")
	private S self = (S)this;
	
	@Override
	public Object value(K key) { return dataMap().value(key); }
	

	@Override
	public S value(K key, Object value)
	{
		dataMap().insert(key, value);
		return self;
	}

	@Override
	public S defaultValue(K key, Object value)
	{
		if (!dataMap().hasKey(key))
			value(key, value);
		return self;
	}

	@Override
	public SpawnedConfigNode<String, S> pushMap(K key)
	{
		defaultValue(key, new JSONObject());
		return new SpawnedConfigNode<String, S>()
				.withParent(self)
				.withDataMap(new JSONObjectBindings()
						.from(valueJSONObject(key)));
	}

	@Override
	public SpawnedConfigNode<Integer, S> pushArray(K key)
	{
		defaultValue(key, new JSONArray());
		return new SpawnedConfigNode<Integer, S>()
				.withParent(self)
				.withDataMap(new JSONArrayBindings()
						.from(valueJSONArray(key)));
	}

	@Override
	public String valueString(K key) { return dataMap().valueString(key); }

	@Override
	public boolean valueBoolean(K key) { return dataMap().valueBoolean(key); }

	@Override
	public int valueInt(K key) { return dataMap().valueInt(key); }

	@Override
	public float valueFloat(K key) { return dataMap().valueFloat(key); }

	@Override
	public double valueDouble(K key) { return dataMap().valueDouble(key); }

	@Override
	public JSONObject valueJSONObject(K key) { return dataMap().valueJSONObject(key); }

	@Override
	public JSONArray valueJSONArray(K key) { return dataMap().valueJSONArray(key); }
	
	
	@Override
	public String toString()
	{
		return dataMap().toString();
	}
	
	@Override
	public String toString(int indentFactor)
	{
		return dataMap().toString(indentFactor);
	}
}
