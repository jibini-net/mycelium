package net.jibini.mycelium.map;

import java.util.ArrayList;
import java.util.List;

public class LinkedElement<K, V>
{
	private int index;

	private List<V> values = new ArrayList<>();
	private LinkedElement<K, V> previous, next;
	
	private boolean hasPrevious = false, hasNext = false;
	
	public LinkedElement<K, V> put(int index, V value)
	{
		this.values.add(index - this.index, value);
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
	
	public LinkedElement<K, V> previous() { return previous; }
	
	public LinkedElement<K, V> next() { return next; }
	
	
	public boolean contains(int hash, boolean mutableIndices)
	{
		return (chunkStart() <= hash && hash <= chunkEnd(mutableIndices));
	}
	
	public boolean claims(int hash, boolean mutableIndices)
	{
		return (chunkStart() <= hash && hash <= claimEnd(mutableIndices));
	}
	
	public List<V> values() { return values; }
	
	
	public V value(int index, boolean mutableIndices)
	{
		if (contains(index, mutableIndices))
		{
			if (mutableIndices)
				return values.get(index - this.index);
			else
				return values.get(0);
		} else
		{
			throw new RuntimeException("Could not find value for key in chunk");
		}
	}
	
	public int chunkStart() { return index; }
	
	
	public int chunkEnd(boolean mutableIndices)
	{
		if (mutableIndices)
			return Math.max(0, index + values.size() - 1);
		else
			return chunkStart();
	}
	
	public int claimEnd(boolean mutableIndices)
	{
		if (mutableIndices)
			return index + values.size();
		else
			return chunkStart();
	}
	
	public boolean hasPrevious() { return hasPrevious; }
	
	public boolean hasNext() { return hasNext; }
}
