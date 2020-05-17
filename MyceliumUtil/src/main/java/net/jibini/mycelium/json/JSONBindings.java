package net.jibini.mycelium.json;

import net.jibini.mycelium.map.KeyValueMap;
import net.jibini.mycelium.map.VariedTypeMap;

public interface JSONBindings<K> extends VariedTypeMap<K>, KeyValueMap<K, Object>
{
	@Override
	String toString();
	
	String toString(int indentFactor);
}
