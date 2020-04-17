package net.jibini.mycelium.conf;

import org.json.JSONArray;
import org.json.JSONObject;

import net.jibini.mycelium.json.DecoratedJSONBindings;
import net.jibini.mycelium.json.JSONArrayBindings;
import net.jibini.mycelium.json.JSONObjectBindings;

@SuppressWarnings("unchecked")
public abstract class AbstractConfigNode<K, ParentType, THIS extends ConfigNode<K, ParentType, ?>> 
		extends DecoratedJSONBindings<K> implements ConfigNode<K, ParentType, THIS>
{
	@Override
	public THIS value(K key, Object value)
	{
		dataMap().insert(key, value);
		return (THIS)this;
	}

	@Override
	public THIS defaultValue(K key, Object value)
	{
		if (!dataMap().hasKey(key))
			value(key, value);
		return (THIS)this;
	}
	
	@Override
	public THIS append(Object value) { dataMap().append(value); return (THIS)this; }
	

	// Also acceptable type: ConfigNode<String, THIS, SpawnedConfigNode<String, THIS>>
	@Override
	public SpawnedConfigNode<String, THIS> pushMap(K key)
	{
		defaultValue(key, new JSONObject());
		return new SpawnedConfigNode<String, THIS>()
				.withParent((THIS)this)
				.withDataMap(new JSONObjectBindings()
						.from(valueJSONObject(key)));
	}

	@Override
	public SpawnedConfigNode<Integer, THIS> pushArray(K key)
	{
		defaultValue(key, new JSONArray());
		return new SpawnedConfigNode<Integer, THIS>()
				.withParent((THIS)this)
				.withDataMap(new JSONArrayBindings()
						.from(valueJSONArray(key)));
	}
}
