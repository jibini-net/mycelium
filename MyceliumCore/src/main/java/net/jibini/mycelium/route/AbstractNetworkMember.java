package net.jibini.mycelium.route;

import java.util.UUID;

@SuppressWarnings("unchecked")
public abstract class AbstractNetworkMember<THIS> implements NetworkMember
{
	private UUID uuid = UUID.randomUUID();
	
	private String name;
	private boolean hasName = false;

	@Override
	public String address()
	{
		if (hasName)
			return name;
		else
			return getClass().getSimpleName() + ':' + uuid.toString();
	}
	
	public THIS withName(String name)
	{
		this.name = name;
		this.hasName = true;
		return (THIS)this;
	}
	
	public UUID uuid() { return uuid; }
}
