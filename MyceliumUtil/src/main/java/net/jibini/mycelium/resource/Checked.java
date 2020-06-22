package net.jibini.mycelium.resource;

public class Checked<V>
{
	private V value;
	private boolean has = false;
	
	private String name;
	private boolean hasName = false;
	
	public V value()
	{
		if (has)
			return value;
		else
			throw new MissingResourceException("Resource value is undefined"
					+ (hasName ? "" : (": '" + name + "'")));
	}
	
	public Checked<V> value(V value)
	{
		this.value = value;
		this.has = true;
		return this;
	}
	
	public Checked<V> withName(String name)
	{
		this.name = name;
		this.hasName = true;
		return this;
	}
	
	public boolean has()
	{ return has; }
}
