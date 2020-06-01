package net.jibini.mycelium.conf;

public interface ConfigNode<K>
{
	ConfigNode<K> value(K key, Object value);
	
	<T> T value(K key);
	
	
	MapConfigNode map(K key);
	
	ArrayConfigNode array(K key);
}
