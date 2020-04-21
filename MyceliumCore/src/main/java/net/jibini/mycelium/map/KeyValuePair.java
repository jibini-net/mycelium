package net.jibini.mycelium.map;

import net.jibini.mycelium.resource.Checked;

public final class KeyValuePair<K, V>
{
	private Checked<K> key = new Checked<K>()
			.withName("Key");
	private Checked<V> value = new Checked<V>()
			.withName("Value");
	
	public KeyValuePair<K, V> withKey(K key)
	{
		this.key.value(key);
		return this;
	}
	
	public KeyValuePair<K, V> withValue(V value)
	{
		this.value.value(value);
		return this;
	}
	
	public K key() { return key.value(); }
	
	public V value() { return value.value(); }
}
