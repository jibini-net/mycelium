package net.jibini.mycelium.map;

import java.util.Iterator;

public final class LinkedArray<V> implements KeyValueMap<Integer, V>
{
	LinkedHashMap<Integer, V> origin = new LinkedHashMap<Integer, V>()
			.withMutableIndices();

	@Override
	public KeyValuePair<Integer, V> keyValue(Integer key)
	{
		return origin.keyValue(key)
				.withKey(key);
	}

	@Override
	public V value(Integer key) { return origin.value(key); }
	
	@Override
	public LinkedArray<V> insert(KeyValuePair<Integer, V> indexValue) { origin.insert(indexValue); return this; }

	@Override
	public LinkedArray<V> insert(Integer index, V value) { origin.insert(index, value); return this; }

	@Override
	public LinkedArray<V> append(V value) { origin.append(value); return this; }

	
	@Override
	public Iterable<KeyValuePair<Integer, V>> iterable()
	{
		return new Iterable<KeyValuePair<Integer, V>>()
				{
					@Override
					public Iterator<KeyValuePair<Integer, V>> iterator()
					{
						LinkedValueIterator<Integer, V, KeyValuePair<Integer, V>> iterator = new LinkedValueIterator<Integer, V, KeyValuePair<Integer, V>>()
								{

									@Override
									public KeyValuePair<Integer, V> iterated(LinkedElement<Integer, V> current, int currentIndex,
											boolean mutableIndices)
									{
										return current.keyValue(currentIndex, mutableIndices)
												.withKey(currentIndex);
									}
							
								};
						if (size() > 0) iterator.withFirst(origin.first());
						iterator.withMutableIndices();
						return iterator;
					}
				};
	}
	
	@Override
	public Iterable<V> values() { return origin.values(); }

	@Override
	public int size() { return origin.size(); }
}
