package net.jibini.mycelium.conf;

import net.jibini.mycelium.map.VariedTypeMap;

public interface ConfigNode<K, P> extends VariedTypeMap<K>
{
	ConfigNode<K, P> value(K key, Object value);
	
	ConfigNode<K, P> defaultValue(K key, Object value);
	
	ConfigNode<K, P> append(Object value);
	
	ConfigNode<String, ?> pushMap(K key);
	
	ConfigNode<Integer, ?> pushArray(K key);
	
	P pop();
	
	String toString(int indentFactor);
	
	String toString();
}
