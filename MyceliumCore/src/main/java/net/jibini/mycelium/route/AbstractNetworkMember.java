package net.jibini.mycelium.route;

import java.util.UUID;

import net.jibini.mycelium.resource.Checked;

@SuppressWarnings("unchecked")
public abstract class AbstractNetworkMember<THIS> implements NetworkMember
{
	private UUID uuid = UUID.randomUUID();
	
	private Checked<String> name = new Checked<String>()
			.withName("Name");

	@Override
	public String address()
	{
		if (name.has())
			return name.value();
		else
			return getClass().getSimpleName() + ':' + uuid().toString();
	}
	
	public THIS withName(String name)
	{
		this.name.value(name);
		return (THIS)this;
	}
	
	public UUID uuid() { return uuid; }
}
