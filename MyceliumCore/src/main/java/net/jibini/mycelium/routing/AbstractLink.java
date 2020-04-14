package net.jibini.mycelium.routing;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLink implements StitchLink
{
	private Logger log = LoggerFactory.getLogger(getClass());

	private List<Request> buffer = new ArrayList<>();
	private List<RequestCallback> persistent = new ArrayList<>();
	private List<RequestCallback> waiting = new ArrayList<>();

	private Object lock = new Object();
	private boolean closed = false;
	
	private Thread negotiator = new Thread(() ->
	{
		while (!closed)
		{
			try
			{
				synchronized(lock)
				{
					lock.wait();
				}
				
				synchronized (buffer)
				{
					if (buffer.size() > 0)
					{
						synchronized (waiting)
						{
							if (waiting.size() > 0)
							{
								int overlap = Math.min(buffer.size(), waiting.size());
								
								for (int i = 0; i < overlap; i ++)
								{
									RequestCallback w = waiting.get(0);
									Request b = buffer.get(0);
									
									Thread asyncCallback = new Thread(() ->
									{
										try
										{
											w.onRequest(this, b);
										} catch (Throwable t)
										{
											log.error("Error occurred in request callback", t);
										}
									});
									
									asyncCallback.setName("AsyncCallback");
									asyncCallback.start();
										
									waiting.remove(0);
									buffer.remove(0);
								}
							}
						}
					}
				}
			} catch (InterruptedException ex)
			{
				log.debug("Async link transfer interrupted");
			}
			
			Thread.yield();
		}
	});
	
	public void startThread()
	{
		negotiator.setName("AsyncNegotiator");
		negotiator.start();
	}
	
	@Override
	public void addPersistentCallback(RequestCallback callback)
	{
		synchronized (persistent)
		{
			persistent.add(callback);
		}
	}
	
	public void triggerPersistent(Request request)
	{
		synchronized (persistent)
		{
			for (RequestCallback c : persistent)
			{
				Thread asyncCallback = new Thread(() ->
				{
					try
					{
						c.onRequest(this, request);
					} catch (Throwable t)
					{
						log.error("Error occurred in request callback", t);
					}
				});
				
				asyncCallback.setName("AsyncCallback");
				asyncCallback.start();
			}
		}
	}
	
	public void pushRequest(Request request)
	{
		synchronized (buffer)
		{
			buffer.add(Request.create(request));
			
			synchronized(getLock())
			{
				getLock().notify();
			}
		}
	}

	@Override
	public void readRequest(RequestCallback callback)
	{
		synchronized (waiting)
		{
			waiting.add(callback);
			
			synchronized(lock)
			{
				lock.notify();
			}
		}
	}
	
	@Override
	public void close()
	{
		closed = true;
		negotiator.interrupt();
	}
	
	public List<Request> getBuffer()  { return buffer; }
	
	public List<RequestCallback> getPersistentCallbacks()  { return persistent; }
	
	public List<RequestCallback> getWaiting()  { return waiting; }
	
	public Object getLock()  { return lock; }
	
	public Thread getNegotiatorThread()  { return negotiator; }
}
