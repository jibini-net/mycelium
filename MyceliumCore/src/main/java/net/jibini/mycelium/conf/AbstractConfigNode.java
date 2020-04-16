package net.jibini.mycelium.conf;

import org.json.JSONArray;
import org.json.JSONObject;

import net.jibini.mycelium.json.DecoratedJSONBindings;
import net.jibini.mycelium.json.JSONArrayBindings;
import net.jibini.mycelium.json.JSONObjectBindings;

public abstract class AbstractConfigNode<K, P, S extends ConfigNode<K, P>> 
		extends DecoratedJSONBindings<K> implements ConfigNode<K, P>
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
	public S append(Object value) { dataMap().append(value); return self; }
	

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
}
