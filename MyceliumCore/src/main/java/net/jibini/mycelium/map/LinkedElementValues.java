package net.jibini.mycelium.map;

import java.util.Iterator;

public class LinkedElementValues<K, V> implements Iterator<V>
{
	private LinkedElement<K, V> current;
	private int currentIndex;
	
	private boolean ready = false, mutableIndices = false;
	
	public LinkedElementValues<K, V> withFirst(LinkedElement<K, V> first)
	{
		this.current = first;
		this.currentIndex = first.chunkStart();
		this.ready = true;
		return this;
	}
	
	public LinkedElementValues<K, V> withMutableIndices() { this.mutableIndices = true; return this; }
	
	
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
	public V next()
	{
		return current.value(currentIndex ++, mutableIndices);
	}
}
