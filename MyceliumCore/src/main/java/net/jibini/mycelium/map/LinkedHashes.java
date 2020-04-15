package net.jibini.mycelium.map;

public class LinkedHashes<K, V>
{
	private int size = 0;
	
	private LinkedElement<K, V> first;
	private boolean mutableIndices = false;
	
	public LinkedHashes<K, V> withMutableIndices() { mutableIndices = true; return this; }
	
	public LinkedHashes<K, V> insertLink(LinkedElement<K, V> previous, K key, V value)
	{
		LinkedElement<K, V> added = new LinkedElement<K, V>()
				.after(previous)
				.withIndex(key.hashCode())
				.put(key, value);
		
		if (previous.hasNext())
		{
			previous.next().after(added);
			added.before(previous.next());
		}
		
		previous.before(added);
		return this;
	}
	
	public LinkedHashes<K, V> insert(K key, V value)
	{
		int index = key.hashCode();
		if (size == 0)
			first = new LinkedElement<K, V>()
				.withIndex(index)
				.put(key, value);
		else if (index < first.chunkStart())
		{
			first.after(new LinkedElement<K, V>()
					.withIndex(index)
					.before(first)
					.put(key, value));
			first = first.previous();
		} else
		{
			LinkedElement<K, V> e = first;
			
			while (true)
			{
				if (mutableIndices)
				{
					boolean nextClaims = false;
					if (e.hasNext()) if (e.next().claims(index)) nextClaims = true;
					if (e.claims(index) && !nextClaims)
						e.put(key, value);
					else if (e.claimEnd() < index && !nextClaims)
						insertLink(e, key, value);
				} else
				{
					boolean nextClaims = false;
					if (e.hasNext()) if (e.next().chunkStart() == index) nextClaims = true;
					if (e.chunkStart() == index)
						e.put(key, value);
					else if (e.chunkStart() < index && !nextClaims)
						insertLink(e, key, value);
				}
				
				if (!e.hasNext())
					break;
				e = e.next();
			}
		}

		size ++;
		return this;
	}
	
	public V value(K key)
	{
		if (size == 0)
			throw new RuntimeException("Size is zero, no values exist");
		else
		{
			LinkedElement<K, V> e = first;
			
			while (true)
			{
				if (e.contains(key.hashCode()))
					return e.value(key, mutableIndices);
				
				if (!e.hasNext())
					break;
				e = e.next();
			}
		}
		throw new RuntimeException("Could not find value for key");
	}
	
	public int size() { return size; }
}
