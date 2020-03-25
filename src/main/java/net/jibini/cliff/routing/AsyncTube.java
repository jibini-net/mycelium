package net.jibini.cliff.routing;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncTube implements StitchLink
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
	
	private AsyncTube()
	{}
	
	public static AsyncTube create()
	{
		AsyncTube result = new AsyncTube();
		result.negotiator.setName("AsyncNegotiator");
		result.negotiator.start();
		return result;
	}
	
	public void sendRequest(Request request)
	{
		synchronized (buffer)
		{
			buffer.add(Request.create(request));
			
			synchronized(lock)
			{
				lock.notify();
			}
			
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
	}

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

	@Override
	public void addPersistentCallback(RequestCallback callback)
	{
		synchronized (persistent)
		{
			persistent.add(callback);
		}
	}
}
