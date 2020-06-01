package net.jibini.mycelium.link.patch;

import net.jibini.mycelium.link.AbstractAddressed;
import net.jibini.mycelium.link.Addressed;
import net.jibini.mycelium.link.Link;

@SuppressWarnings("unchecked")
public abstract class AbstractPatch<T, THIS extends Patch<T>> extends AbstractAddressed<THIS>
		implements Patch<T>, Addressed
{
	Link<T> exposed = new Link<T>()
			{
				@Override
				public Link<T> send(T value)
				{ down().push(value); return this; }
		
				@Override
				public T read()
				{ return up().pull(); }

				@Override
				public boolean isAlive()
				{ return up().isAlive() && down().isAlive(); }
				

				@Override
				public Link<T> close()
				{
					up().close();
					down().close();
					return this;
				}
			};
	
	@Override
	public THIS close()
	{
		up().close();
		down().close();
		return (THIS)this;
	}

	@Override
	public THIS send(T value)
	{ up().push(value); return (THIS)this; }

	@Override
	public T read()
	{ return down().pull(); }

	@Override
	public boolean isAlive()
	{ return up().isAlive() && down().isAlive(); }

	@Override
	public Link<T> uplink()
	{ return exposed; }
}
