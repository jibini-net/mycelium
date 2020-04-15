package net.jibini.mycelium.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LinkedElement<K, V> implements Iterator<LinkedElement<K, V>>
{
	private int index;

	private List<V> values = new ArrayList<>();
	private LinkedElement<K, V> previous, next;
	
	private boolean hasPrevious = false, hasNext = false;
	
	public LinkedElement<K, V> put(K key, V value)
	{
		this.values.add(key.hashCode() - index, value);
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
	
	@Override
	public LinkedElement<K, V> next() { return next; }
	
	public boolean contains(int hash) { return (chunkStart() <= hash && hash <= chunkEnd()); }
	
	public boolean claims(int hash) { return (chunkStart() <= hash && hash <= claimEnd()); }
	
	public V value(K key, boolean mutableIndices)
	{
		int hash = key.hashCode();
		
		if (contains(hash))
		{
			if (mutableIndices)
				return values.get(hash - index);
			else
				return values.get(0);
		} else
		{
			throw new RuntimeException("Could not find value for key in chunk");
		}
	}
	
	public int chunkStart() { return index; }
	
	public int chunkEnd() { return Math.max(0, index + values.size() - 1); }
	
	public int claimEnd() { return index + values.size(); }
	
	public boolean hasPrevious() { return hasPrevious; }
	
	@Override
	public boolean hasNext() { return hasNext; }
}
