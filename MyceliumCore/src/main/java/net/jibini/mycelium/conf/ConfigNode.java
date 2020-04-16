package net.jibini.mycelium.conf;

import net.jibini.mycelium.map.KeyValueMap;

public interface ConfigNode<K, P>
{
	Object value(K key);
	
	ConfigNode<K, P> value(K key, Object value);
	
	ConfigNode<K, P> defaultValue(K key, Object value);
	
	ConfigNode<String, ?> pushMap(K key);
	
	ConfigNode<Integer, ?> pushArray(K key);
	
	P pop();
	
	KeyValueMap<K, Object> dataMap();
}
