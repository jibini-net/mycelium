package net.jibini.mycelium.conf;

import net.jibini.mycelium.error.EndOfStackException;
import net.jibini.mycelium.error.MissingResourceException;
import net.jibini.mycelium.json.JSONBindings;
import net.jibini.mycelium.resource.Checked;

public class SpawnedConfigNode<K, P> extends AbstractConfigNode<K, P, SpawnedConfigNode<K, P>>
{
	private Checked<P> parent = new Checked<P>()
			.withName("Parent Node");
	private Checked<JSONBindings<K>> dataMap = new Checked<JSONBindings<K>>()
			.withName("Data Map");
	
	public SpawnedConfigNode<K, P> withParent(P parent)
	{
		this.parent.value(parent);
		return this;
	}
	
	public SpawnedConfigNode<K, P> withDataMap(JSONBindings<K> dataMap)
	{
		this.dataMap.value(dataMap);
		return this;
	}
	
	@Override
	public P pop()
	{
		try
		{
			return parent.value();
		} catch (MissingResourceException ex)
		{
			throw new EndOfStackException("Cannot pop, node is orphaned", ex);
		}
	}
	
	@Override
	public JSONBindings<K> dataMap() { return dataMap.value(); }
}
