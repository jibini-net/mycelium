package net.jibini.mycelium.map;

public interface KeyValueMap<K, V> extends DirectMap<K, V>
{
	KeyValuePair<K, V> keyValue(K key);
	
	KeyValueMap<K, V> insert(KeyValuePair<K, V> keyValue);
	
	KeyValueMap<K, V> insert(K key, V value);
	
	KeyValueMap<K, V> append(V value);
	
	int size();
	
	boolean hasKey(K key);
	
	boolean hasValue(V value);
	
	Iterable<KeyValuePair<K, V>> iterable();
	
	Iterable<V> values();
}
