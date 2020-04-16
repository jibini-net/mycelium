package net.jibini.mycelium.map;

public final class KeyValuePair<K, V>
{
	private K key;
	private V value;
	
	private boolean hasKey = false, hasValue = false;
	
	public KeyValuePair<K, V> withKey(K key)
	{
		this.key = key;
		this.hasKey = true;
		return this;
	}
	
	public KeyValuePair<K, V> withValue(V value)
	{
		this.value = value;
		this.hasValue = true;
		return this;
	}
	
	public K key()
	{
		if (hasKey)
			return key;
		throw new RuntimeException("Key-value pair has no key");
	}
	
	public V value()
	{
		if (hasValue)
			return value;
		throw new RuntimeException("Key-value pair has no value");
	}
}
