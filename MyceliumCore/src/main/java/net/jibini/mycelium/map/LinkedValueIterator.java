package net.jibini.mycelium.map;

import java.util.Iterator;

public abstract class LinkedValueIterator<K, V, O> implements Iterator<O>
{
	private LinkedElement<K, V> current;
	private int currentIndex;
	
	private boolean ready = false, mutableIndices = false;
	
	public LinkedValueIterator<K, V, O> withFirst(LinkedElement<K, V> first)
	{
		this.current = first;
		this.currentIndex = first.chunkStart();
		this.ready = true;
		return this;
	}
	
	public LinkedValueIterator<K, V, O> withMutableIndices() { this.mutableIndices = true; return this; }
	
	
	@Override
	public boolean hasNext()
	{
		if (!ready)
			return false;
		if (current.contains(currentIndex, mutableIndices))
			return true;
		
		if (current.hasNext())
		{
			current = current.next();
			currentIndex = current.chunkStart();
			return true;
		} else
			return false;
	}

	@Override
	public O next() { return iterated(current, currentIndex ++, mutableIndices); }
	
	
	public abstract O iterated(LinkedElement<K, V> current, int currentIndex, boolean mutableIndices);
}
