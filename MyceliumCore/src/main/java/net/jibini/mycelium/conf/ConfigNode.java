package net.jibini.mycelium.conf;

import net.jibini.mycelium.json.JSONBinding;
import net.jibini.mycelium.map.VariedTypeMap;

public interface ConfigNode<K, P> extends VariedTypeMap<K>
{
	Object value(K key);
	
	ConfigNode<K, P> value(K key, Object value);
	
	ConfigNode<K, P> defaultValue(K key, Object value);
	
	ConfigNode<String, ?> pushMap(K key);
	
	ConfigNode<Integer, ?> pushArray(K key);
	
	P pop();
	
	JSONBinding<K> dataMap();
	
	String toString(int indentFactor);
	
	String toString();
}
