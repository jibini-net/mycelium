package net.jibini.mycelium.map;

public interface DirectMap<K, V>
{
	V value(K key);
}
