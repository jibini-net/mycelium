package net.jibini.cliff.routing;

public class AsyncPatch implements Patch
{
	private AsyncTube upstream = AsyncTube.create();
	private AsyncTube downstream = AsyncTube.create();
	
	private AsyncPatch()
	{}
	
	public static AsyncPatch create()
	{
		return new AsyncPatch();
	}

	@Override
	public StitchLink getUpstream()
	{
		return new StitchLink()
		{

			@Override
			public void sendRequest(Request request)
			{
				downstream.sendRequest(request);
			}

			@Override
			public void readRequest(RequestCallback callback)
			{
				upstream.readRequest(callback);
			}

			@Override
			public void addPersistentCallback(RequestCallback callback)
			{
				upstream.addPersistentCallback(callback);
			}

			@Override
			public void close()
			{
				AsyncPatch.this.close();
			}
			
		};
	}

	@Override
	public StitchLink getDownstream()
	{
		return new StitchLink()
		{

			@Override
			public void sendRequest(Request request)
			{
				upstream.sendRequest(request);
			}

			@Override
			public void readRequest(RequestCallback callback)
			{
				downstream.readRequest(callback);
			}

			@Override
			public void addPersistentCallback(RequestCallback callback)
			{
				downstream.addPersistentCallback(callback);
			}

			@Override
			public void close()
			{
				AsyncPatch.this.close();
			}
			
		};
	}

	@Override
	public void close()
	{
		upstream.close();
		downstream.close();
	}
}
