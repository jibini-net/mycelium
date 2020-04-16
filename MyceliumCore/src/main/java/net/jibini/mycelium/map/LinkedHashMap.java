package net.jibini.mycelium.map;

import java.util.Iterator;

public final class LinkedHashMap<K, V> implements KeyValueMap<K, V>
{
	private int linkCount = 0;
	private int size = 0;
	
	private LinkedElement<K, V> first;
	private boolean mutableIndices = false;
	
	public LinkedHashMap<K, V> withMutableIndices() { mutableIndices = true; return this; }
	
	public LinkedHashMap<K, V> insertLink(LinkedElement<K, V> previous, int hash, KeyValuePair<K, V> keyValue)
	{
		LinkedElement<K, V> added = new LinkedElement<K, V>()
				.after(previous)
				.withIndex(hash)
				.put(hash, keyValue);
		
		if (previous.hasNext())
		{
			added.before(previous.next());
			previous.next().after(added);
		}

		linkCount ++;
		previous.before(added);
		return this;
	}
	
	public LinkedHashMap<K, V> resetChain(int hash, KeyValuePair<K, V> keyValue)
	{
		this.first = new LinkedElement<K, V>()
				.withIndex(hash)
				.put(hash, keyValue);
			size = 1;
			linkCount = 1;
		return this;
	}
	
	public LinkedHashMap<K, V> insertHashed(int hash, KeyValuePair<K, V> keyValue)
	{
		if (size() == 0)
			resetChain(hash, keyValue);
		else if (hash < first.chunkStart())
		{
			first.after(new LinkedElement<K, V>()
					.withIndex(hash)
					.before(first)
					.put(hash, keyValue));
			first = first.previous();
			linkCount ++;
		} else
		{
			LinkedElement<K, V> e = first;
			
			for (int i = 0; i < linkCount(); i++)
			{
				boolean nextClaims = false, nextTooFar = true;
				
				if (e.hasNext())
				{
					if (e.next().claims(hash, mutableIndices)) nextClaims = true;
					if (e.next().chunkStart() <= hash) nextTooFar = false;
				}
				
				if (e.claims(hash, mutableIndices) && !nextClaims)
				{
					e.put(hash, keyValue);
					break;
				} else if (e.claimEnd(mutableIndices) < hash && nextTooFar)
				{
					insertLink(e, hash, keyValue);
					break;
				}

				if (e.hasNext())
					e = e.next();
			}
		}

		size ++;
		return this;
	}
	
	@Override
	public LinkedHashMap<K, V> insert(K key, V value)
	{
		return insertHashed(key.hashCode(), new KeyValuePair<K, V>()
				.withKey(key)
				.withValue(value));
	}

	@Override
	public KeyValueMap<K, V> insert(KeyValuePair<K, V> keyValue) { return insert(keyValue.key(), keyValue.value()); }
	
	public int lastHash()
	{
		LinkedElement<K, V> e = first;
		for (int i = 0; i < linkCount() - 1; i++)
			e = e.next();
		return e.chunkEnd(mutableIndices);
	}
	
	public LinkedHashMap<K, V> append(V value)
	{
		if (mutableIndices)
		{
			if (size == 0)
				return insertHashed(0, new KeyValuePair<K, V>()
						.withValue(value));
			return insertHashed(lastHash() + 1, new KeyValuePair<K, V>()
					.withValue(value));
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
			
			for (int i = 0; i < linkCount(); i++)
			{
				if (e.contains(hash, mutableIndices))
					return e.value(hash, mutableIndices);
				if (e.hasNext())
					e = e.next();
			}
		}
		
		throw new RuntimeException("Could not find value for key");
	}
	
	@Override
	public V value(K key) { return valueHashed(key.hashCode()); }
	
	@Override
	public int size() { return size; }
	
	public int linkCount() { return linkCount; }
	
	public LinkedElement<K, V> first() { return first; }
	
	@Override
	public Iterable<V> values()
	{
		return new Iterable<V>()
				{
					@Override
					public Iterator<V> iterator()
					{
						LinkedValueIterator<K, V, V> iterator = new LinkedValueIterator<K, V, V>()
								{

									@Override
									public V iterated(LinkedElement<K, V> current, int currentIndex,
											boolean mutableIndices)
									{ return current.value(currentIndex, mutableIndices); }
							
								};
						if (size() > 0) iterator.withFirst(first);
						if (mutableIndices) iterator.withMutableIndices();
						return iterator;
					}
				};
	}

	@Override
	public KeyValuePair<K, V> keyValue(K key)
	{
		return new KeyValuePair<K, V>()
				.withKey(key)
				.withValue(value(key));
	}
	
	@Override
	public Iterable<KeyValuePair<K, V>> iterable()
	{
		return new Iterable<KeyValuePair<K, V>>()
				{
					@Override
					public Iterator<KeyValuePair<K, V>> iterator()
					{
						LinkedValueIterator<K, V, KeyValuePair<K, V>> iterator = new LinkedValueIterator<K, V, KeyValuePair<K, V>>()
								{

									@Override
									public KeyValuePair<K, V> iterated(LinkedElement<K, V> current, int currentIndex,
											boolean mutableIndices)
									{ return current.keyValue(currentIndex, mutableIndices); }
							
								};
						if (size() > 0) iterator.withFirst(first);
						if (mutableIndices) iterator.withMutableIndices();
						return iterator;
					}
				};
	}

	@Override
	public boolean hasKey(K key)
	{
		try
		{
			value(key);
			return true;
		} catch (RuntimeException ex)
		{
			return false;
		}
	}

	@Override
	public boolean hasValue(V value)
	{
		for (V v : values())
			if (v.equals(value))
				return true;
		return false;
	}
}
