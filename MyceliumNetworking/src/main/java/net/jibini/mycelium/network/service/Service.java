package net.jibini.mycelium.network.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.mycelium.api.Request;
import net.jibini.mycelium.network.session.Session;
import net.jibini.mycelium.network.session.SessionKernel;
import net.jibini.mycelium.network.session.SessionManager;
import net.jibini.mycelium.routing.StitchLink;

public class Service
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private Session session = null;
	private SessionManager manager = null;
	private StitchLink mycelium;
	
	private Object creationLock = new Object();
	
	private Service()
	{}
	
	public static Service create(StitchLink mycelium, String target, Class<? extends SessionKernel> kernel)
	{
		Service result = new Service();
		result.mycelium = mycelium;
		
		Request createSession = new Request()
				.withTarget(target)
				.withRequest("CreateSession");
		createSession.header().put("session", UUID.randomUUID().toString());
		mycelium.sendRequest(createSession);
		
		mycelium.readRequest((source, request) ->
		{
			Session session = Session.create(kernel, request.header().getString("session"),
					request.response().getString("token"));
			result.session = session;
			result.manager = SessionManager.create(result);
			
			synchronized (result.creationLock)
			{
				result.creationLock.notifyAll();
			}
		});
		
		return result;
	}
	
	public Session getSession()  { return session; }
	
	public SessionManager getSessionManager()  { return manager; }
	
	public void waitSessionCreation()
	{
		if (!isSessionCreated())
			synchronized (creationLock)
			{
				try
				{
					creationLock.wait();
				} catch (InterruptedException ex)
				{
					log.error("Service creation lock interrupted", ex);
				}
			}
	}
	
	public boolean isSessionCreated()
	{
		return creationLock == null;
	}

	public void sendRequest(Request request)
	{
		session.embed(request);
		mycelium.sendRequest(request);
	}

//	public void readRequest(RequestCallback callback)
//	{
//		cliff.readRequest(callback);
//	}
//	
//	private Request syncRead;
//	
//	public synchronized Request readRequest()
//	{
//		syncRead = null;
//		Object lock = new Object();
//		
//		readRequest((source, request) ->
//		{
//			syncRead = request;
//			
//			synchronized (lock)
//			{
//				lock.notify();
//			}
//		});
//		
//		synchronized (lock)
//		{
//			try
//			{
//				lock.wait();
//			} catch (InterruptedException ex)
//			{
//				log.error("Request read waiting interrupted", ex);
//			}
//		}
//		
//		return syncRead;
//	}
	
	public StitchLink getMycelium()  { return mycelium; }
}
