package net.jibini.mycelium.routing;

import net.jibini.mycelium.api.Request;

public class AsyncTube extends AbstractLink
{
	private AsyncTube()
	{}
	
	public static AsyncTube create()
	{
		AsyncTube result = new AsyncTube();
		result.startThread();
		return result;
	}
	
	public void sendRequest(Request request)
	{
		synchronized (getBuffer())
		{
			pushRequest(request);
			triggerPersistent(request);
		}
	}

	@Override
	public void close()
	{
		super.close();
	}
}
