package net.jibini.mycelium.map;

import java.util.ArrayList;
import java.util.List;

public final class LinkedElement<K, V>
{
	private int index;

	private List<KeyValuePair<K, V>> keyValues = new ArrayList<>();
	private LinkedElement<K, V> previous, next;
	
	private boolean hasPrevious = false, hasNext = false;
	
	public LinkedElement<K, V> put(int index, KeyValuePair<K, V> keyValue)
	{
		this.keyValues().add(index - this.index, keyValue);
		return this;
	}
	
	public LinkedElement<K, V> before(LinkedElement<K, V> next)
	{
		this.next = next;
		hasNext = true;
		return this;
	}
	
	public LinkedElement<K, V> after(LinkedElement<K, V> previous)
	{
		this.previous = previous;
		hasPrevious = true;
		return this;
	}
	
	public LinkedElement<K, V> withIndex(int index)
	{
		this.index = index;
		return this;
	}
	
	public LinkedElement<K, V> previous()
	{
		if (hasPrevious)
			return previous;
		else
			throw new RuntimeException("Chunk has no previous link");
	}
	
	public LinkedElement<K, V> next()
	{
		return next;
	}
	
	
	public boolean contains(int hash, boolean mutableIndices)
	{
		return (chunkStart() <= hash && hash <= chunkEnd(mutableIndices));
	}
	
	public boolean claims(int hash, boolean mutableIndices)
	{
		return (chunkStart() <= hash && hash <= claimEnd(mutableIndices));
	}
	
	public List<KeyValuePair<K, V>> keyValues() { return keyValues; }
	
	
	public V value(int index, boolean mutableIndices)
	{
		if (contains(index, mutableIndices))
		{
			if (mutableIndices)
				return keyValues().get(index - this.index).value();
			else
				return keyValues().get(0).value();
		} else
			throw new RuntimeException("Could not find value for key in chunk");
	}
	
	public KeyValuePair<K, V> keyValue(int index, boolean mutableIndices)
	{
		if (contains(index, mutableIndices))
		{
			if (mutableIndices)
				return new KeyValuePair<K, V>()
						.withValue(value(index, mutableIndices));
			else
				return keyValues().get(0);
		} else
			throw new RuntimeException("Could not find value for key in chunk");
	}
	
	public int chunkStart() { return index; }
	
	
	public int chunkEnd(boolean mutableIndices)
	{
		if (mutableIndices)
			return Math.max(0, index + keyValues().size() - 1);
		else
			return chunkStart();
	}
	
	public int claimEnd(boolean mutableIndices)
	{
		if (mutableIndices)
			return index + keyValues().size();
		else
			return chunkStart();
	}
	
	public boolean hasPrevious() { return hasPrevious; }
	
	public boolean hasNext() { return hasNext; }
}
