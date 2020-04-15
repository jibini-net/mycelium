package net.jibini.mycelium.map;

import java.util.Iterator;

public class LinkedHashes<K, V>
{
	private int size = 0;
	
	private LinkedElement<K, V> first;
	private boolean mutableIndices = false;
	
	public LinkedHashes<K, V> withMutableIndices() { mutableIndices = true; return this; }
	
	public LinkedHashes<K, V> insertLink(LinkedElement<K, V> previous, int hash, V value)
	{
		LinkedElement<K, V> added = new LinkedElement<K, V>()
				.after(previous)
				.withIndex(hash)
				.put(hash, value);
		
		if (previous.hasNext())
		{
			added.before(previous.next());
			previous.next().after(added);
		}
		
		previous.before(added);
		return this;
	}
	
	public LinkedHashes<K, V> resetChain(int hash, V first)
	{
		this.first = new LinkedElement<K, V>()
			.withIndex(hash)
			.put(hash, first);
		size = 1;
		return this;
	}
	
	public LinkedHashes<K, V> insertHashed(int hash, V value)
	{
		if (size() == 0)
			resetChain(hash, value);
		else if (hash < first.chunkStart())
		{
			first.after(new LinkedElement<K, V>()
					.withIndex(hash)
					.before(first)
					.put(hash, value));
			first = first.previous();
		} else
		{
			LinkedElement<K, V> e = first;
			
			while (true)
			{
					boolean nextClaims = false, nextTooFar = true;
					
					if (e.hasNext())
					{
						if (e.next().claims(hash, mutableIndices)) nextClaims = true;
						if (e.next().chunkStart() <= hash) nextTooFar = false;
					}
					
					if (e.claims(hash, mutableIndices) && !nextClaims)
					{
						e.put(hash, value);
						break;
					} else if (e.claimEnd(mutableIndices) < hash && nextTooFar)
					{
						insertLink(e, hash, value);
						break;
					}
				
				if (e.hasNext())
					e = e.next();
				else
					throw new RuntimeException("Internal error, failed to place insert");
			}
		}

		size ++;
		return this;
	}
	
	public LinkedHashes<K, V> insert(K key, V value) { return insertHashed(key.hashCode(), value); }
	
	
	public int lastHash()
	{
		LinkedElement<K, V> e = first;
		
		while (true)
		{
			if (!e.hasNext())
				return e.chunkEnd(mutableIndices);
			e = e.next();
		}
	}
	
	public LinkedHashes<K, V> append(V value)
	{
		if (mutableIndices)
		{
			if (size == 0)
				return insertHashed(0, value);
			return insertHashed(lastHash() + 1, value);
		} else
			throw new RuntimeException("May only append an index with mutable indices");
	}
	
	public V valueHashed(int hash)
	{
		if (size() == 0)
			throw new RuntimeException("Size is zero, no values exist");
		else
		{
			LinkedElement<K, V> e = first;
			
			while (true)
			{
				if (e.contains(hash, mutableIndices))
					return e.value(hash, mutableIndices);
				
				if (!e.hasNext())
					break;
				e = e.next();
			}
		}
		
		throw new RuntimeException("Could not find value for key");
	}
	
	public V value(K key) { return valueHashed(key.hashCode()); }
	
	public int size() { return size; }
	

	public Iterable<V> values()
	{
		return new Iterable<V>()
				{
					@Override
					public Iterator<V> iterator()
					{
						LinkedElementValues<K, V> iterator = new LinkedElementValues<K, V>();
						if (size() > 0) iterator.withFirst(first);
						if (mutableIndices) iterator.withMutableIndices();
						return iterator;
					}
				};
	}
}
