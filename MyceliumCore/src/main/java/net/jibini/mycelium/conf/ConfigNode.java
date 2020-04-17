package net.jibini.mycelium.conf;

import net.jibini.mycelium.map.VariedTypeMap;

public interface ConfigNode<K, ParentType, THIS extends ConfigNode<K, ParentType, ?>>
		extends VariedTypeMap<K>
{
	THIS value(K key, Object value);
	
	THIS defaultValue(K key, Object value);
	
	THIS append(Object value);
	
	ConfigNode<String, THIS, ?> pushMap(K key);
	
	ConfigNode<Integer, THIS, ?> pushArray(K key);
	
	ParentType pop();
	
	String toString(int indentFactor);
	
	String toString();
}
