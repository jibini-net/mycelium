package net.jibini.mycelium.conf;

import net.jibini.mycelium.map.LinkedArray;
import net.jibini.mycelium.map.LinkedHashMap;

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

	@SuppressWarnings("unchecked")
	@Override
	public SpawnedConfigNode<String, S> pushMap(K key)
	{
		// Store JSONObject with given key (not node)
		defaultValue(key, new SpawnedConfigNode<String, S>()
				.withParent(self)
				.withDataMap(new LinkedHashMap<String, Object>()));
		return (SpawnedConfigNode<String, S>) value(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public SpawnedConfigNode<Integer, S> pushArray(K key)
	{
		// Store JSONArray with given key (not node)
		defaultValue(key, new SpawnedConfigNode<Integer, S>()
				.withParent(self)
				.withDataMap(new LinkedArray<Object>()));
		return (SpawnedConfigNode<Integer, S>) value(key);
	}
}
