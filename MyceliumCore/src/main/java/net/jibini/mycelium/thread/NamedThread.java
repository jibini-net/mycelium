package net.jibini.mycelium.thread;

public final class NamedThread
{
	private Thread origin = new Thread();
	private Throwable thrown;
	private boolean hasException = false;
	
	public NamedThread() { origin.setDaemon(false); }
	
	
	public NamedThread withRunnable(Runnable runnable)
	{
		String name = origin.getName();
		boolean daemon = origin.isDaemon();
		
		origin = new Thread(runnable);
		origin.setDaemon(daemon);
		
		origin.setUncaughtExceptionHandler((Thread t, Throwable ex) ->
		{
			thrown = ex;
			hasException = true;
		});
		
		return withName(name);
	}
	
	public NamedThread asDaemon() { origin().setDaemon(true); return this; }
	
	public NamedThread withName(String name) { origin().setName(name); return this; }
	
	
	public NamedThread start() { origin().start(); return this; }
	
	public NamedThread interrupt() { origin().interrupt(); return this; }
	
	public boolean isAlive() { return origin().isAlive(); }
	
	public Thread origin() { return origin; }
	
	
	public NamedThread checkException() throws Throwable
	{
		if (hasException)
			throw thrown;
		return this;
	}
}
