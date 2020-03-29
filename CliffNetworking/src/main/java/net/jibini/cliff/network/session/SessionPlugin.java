package net.jibini.cliff.network.session;

import net.jibini.cliff.api.RequestHandler;
import net.jibini.cliff.plugin.AbstractCliffPlugin;

public abstract class SessionPlugin extends AbstractCliffPlugin
{
	private SessionManager sessionManager;
	
	public abstract Class<? extends SessionKernel> getKernelClass();
	
	@Override
	public void registerRequests(RequestHandler requestHandler)
	{
		sessionManager = SessionManager.create(this);
		requestHandler.attachRequestCallback(null, sessionManager);
	}
	
	public SessionManager getSessionManager()  { return sessionManager; }
}
