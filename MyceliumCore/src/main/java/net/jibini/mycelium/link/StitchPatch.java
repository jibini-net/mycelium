package net.jibini.mycelium.link;

import net.jibini.mycelium.api.Request;
import net.jibini.mycelium.error.NetworkException;
import net.jibini.mycelium.error.RoutingException;
import net.jibini.mycelium.route.AbstractNetworkMember;

public final class StitchPatch extends AbstractNetworkMember<StitchPatch>
		implements StitchLink
{
	private Request incoming, outgoing;
	private boolean hasIncoming = false;
	private boolean hasOutgoing = false;
	private Object updateLock = new Object();

	boolean alive = true;
	
	private void lock()
	{
		try
		{
			updateLock.wait();
		} catch (InterruptedException ex)
		{
			throw new RoutingException("Error in update lock", ex);
		}
	}
	
	private void unlock()
	{
		updateLock.notifyAll();
	}
	
	StitchLink exposed = new StitchLink()
			{
				@Override
				public StitchLink send(Request request)
				{
					synchronized(updateLock)
					{
						unlock();
						while (hasIncoming && isAlive()) lock();
						
						if (isAlive())
						{
							incoming = request;
							hasIncoming = true;
							unlock();
							
							return this;
						} else
							throw new NetworkException("Patch is closed or closing");
					}
				}
		
				@Override
				public Request read()
				{
					synchronized(updateLock)
					{
						unlock();
						while (!hasOutgoing && isAlive()) lock();
						
						if (isAlive())
						{
							hasOutgoing = false;
							return outgoing;
						} else
							throw new NetworkException("Patch is closed or closing");
					}
				}

				@Override
				public boolean isAlive()
				{
					return alive;
				}

				@Override
				public StitchLink close()
				{
					alive = false;
					return this;
				}
			};

	@Override
	public StitchPatch close()
	{
		synchronized(updateLock)
		{
			alive = false;
			unlock();
			return this;
		}
	}

	@Override
	public StitchPatch send(Request request)
	{
		synchronized(updateLock)
		{
			unlock();
			while (hasOutgoing && isAlive()) lock();
			
			if (isAlive())
			{
				outgoing = request;
				hasOutgoing = true;
				unlock();
				
				return this;
			} else
				throw new NetworkException("Patch is closed or closing");
		}
	}

	@Override
	public Request read()
	{
		synchronized(updateLock)
		{
			unlock();
			while (!hasIncoming && isAlive()) lock();
			
			if (isAlive())
			{
				hasIncoming = false;
				return incoming;
			} else
				throw new NetworkException("Patch is closed or closing");
		}
	}

	@Override
	public boolean isAlive() { return alive; }

	@Override
	public StitchLink link() { return exposed; }
}
