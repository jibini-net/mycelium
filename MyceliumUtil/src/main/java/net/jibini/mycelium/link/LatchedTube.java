package net.jibini.mycelium.link;

import net.jibini.mycelium.error.UpdateException;
import net.jibini.mycelium.thread.UpdateLatch;

public class LatchedTube<T> implements Tube<T>
{
	private UpdateLatch latch = new UpdateLatch();
	
	private boolean has = false;
	private T value;
	
	private boolean alive = true;
	
	@Override
	public Tube<T> push(T value)
	{
		latch.synchronize(() ->
		{
			latch.unlock();
			while (has && isAlive()) latch.lock();
			
			if (isAlive())
			{
				this.value = value;
				this.has = true;
				latch.unlock();
			} else
				throw new UpdateException("Tube update failed, tube is closing");
		});
		
		return this;
	}

	@Override
	public T pull()
	{
		latch.synchronize(() ->
		{
			latch.unlock();
			while (!has && isAlive()) latch.lock();
			
			if (isAlive())
				this.has = false;
			else
				throw new UpdateException("Tube update failed, tube is closing");
		});
		
		return value;
	}

	@Override
	public boolean isAlive() { return alive; }

	
	@Override
	public Closeable close()
	{
		latch.synchronize(() ->
		{
			alive = false;
			latch.unlock();
		});
		
		return this;
	}
}
