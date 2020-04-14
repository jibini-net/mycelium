package net.jibini.mycelium.routing;

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
				upstream.readRequest((s, r) ->
				{
					callback.onRequest(this, r);
				});
			}

			@Override
			public void addPersistentCallback(RequestCallback callback)
			{
				upstream.addPersistentCallback((s, r) ->
				{
					callback.onRequest(this, r);
				});
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
				downstream.readRequest((s, r) ->
				{
					callback.onRequest(this, r);
				});
			}

			@Override
			public void addPersistentCallback(RequestCallback callback)
			{
				downstream.addPersistentCallback((s, r) ->
				{
					callback.onRequest(this, r);
				});
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
