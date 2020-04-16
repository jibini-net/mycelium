package net.jibini.mycelium.conf;

import net.jibini.mycelium.json.JSONBinding;

public final class SpawnedConfigNode<K, P> extends AbstractConfigNode<K, P, SpawnedConfigNode<K, P>>
{
	private P parent;
	private boolean hasParent = false;
	
	private JSONBinding<K> dataMap;
	private boolean hasDataMap = false;
	
	public SpawnedConfigNode<K, P> withParent(P parent)
	{
		this.parent = parent;
		this.hasParent = true;
		return this;
	}
	
	public SpawnedConfigNode<K, P> withDataMap(JSONBinding<K> dataMap)
	{
		this.dataMap = dataMap;
		this.hasDataMap = true;
		return this;
	}
	
	@Override
	public P pop()
	{
		if (hasParent)
			return parent;
		else
			throw new RuntimeException("Cannot pop config node, is orphaned");
	}

	@Override
	public JSONBinding<K> dataMap()
	{
		if (hasDataMap)
			return dataMap;
		else
			throw new RuntimeException("Spawned config node was not given a data map");
	}
}
