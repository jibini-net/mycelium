package net.jibini.mycelium.network.session;

import net.jibini.mycelium.api.RequestHandler;
import net.jibini.mycelium.plugin.AbstractSpore;

public abstract class SessionPlugin extends AbstractSpore
{
	private SessionManager sessionManager;
	
	public abstract Class<? extends SessionKernel> getKernelClass();
	
	@Override
	public void registerRequests(RequestHandler requestHandler)
	{
		sessionManager = SessionManager.create(this);
	}
	
	public SessionManager getSessionManager()  { return sessionManager; }
}
