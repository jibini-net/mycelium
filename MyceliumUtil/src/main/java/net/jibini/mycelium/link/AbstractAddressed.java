package net.jibini.mycelium.link;

import java.util.UUID;

import net.jibini.mycelium.resource.Checked;

@SuppressWarnings("unchecked")
public class AbstractAddressed<THIS> implements Addressed
{
	private Checked<String> address = new Checked<String>()
			.withName("Address");
	private final UUID uuid = UUID.randomUUID();
	
	@Override
	public String address()
	{
		if (address.has())
			return address.value();
		else
			return getClass().getSimpleName() + ':' + uuid().toString();
	}

	@Override
	public UUID uuid()
	{ return uuid; }
	
	public THIS withName(String name)
	{ address.value(name); return (THIS)this; }
}
