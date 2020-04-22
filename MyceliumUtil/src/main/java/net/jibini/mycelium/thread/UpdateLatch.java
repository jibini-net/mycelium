package net.jibini.mycelium.thread;

import net.jibini.mycelium.error.UpdateException;

public class UpdateLatch
{
	private Object updateLock = new Object();
	
	public void lock()
	{
		try
		{
			object().wait();
		} catch (InterruptedException ex)
		{
			throw new UpdateException("Error in update lock", ex);
		}
	}
	
	public void unlock()
	{
		object().notifyAll();
	}
	
	public Object object() { return updateLock; }
	
	
	public UpdateLatch synchronize(Runnable run)
	{
		synchronized(object())
		{
			run.run();
			return this;
		}
	}
}
