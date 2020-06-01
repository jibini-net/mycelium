package net.jibini.mycelium.link;

import net.jibini.mycelium.api.Request;

public interface StitchLink extends Link<Request>
{
	public static StitchLink from(Link<Request> link)
	{
		return new StitchLink()
				{
					@Override
					public StitchLink send(Request request)
					{ link.send(request); return this; }

					@Override
					public Request read()
					{ return link.read(); }

					@Override
					public boolean isAlive()
					{ return link.isAlive(); }

					@Override
					public StitchLink close()
					{ link.close(); return this; }
				};
	}
}
