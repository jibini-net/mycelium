package net.jibini.mycelium.network.session;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.mycelium.api.RequestHandler;
import net.jibini.mycelium.api.ResponderCallback;
import net.jibini.mycelium.network.service.Service;
import net.jibini.mycelium.routing.AsyncPatch;
import net.jibini.mycelium.routing.Patch;
import net.jibini.mycelium.routing.Request;
import net.jibini.mycelium.routing.RequestCallback;
import net.jibini.mycelium.routing.RequestRouter;
import net.jibini.mycelium.routing.StitchLink;

public class SessionManager implements RequestCallback
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	// Server-side
	private SessionPlugin plugin;
	private RequestRouter pluginRouter;
	private boolean serverSide = false;
	
	// Server- and client-side
	private Map<String, Session> sessions = new HashMap<>();
	private RequestRouter sessionRouter = RequestRouter.create("target", true);
	
	private RequestHandler handler = RequestHandler.create();
	private Class<? extends SessionKernel> kernelClass;

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Handler
	{
		String value();
	}
	
	private SessionManager()
	{}
	
	public static SessionManager create(SessionPlugin plugin)
	{
		SessionManager result = new SessionManager();
		result.serverSide = true;
		
		result.plugin = plugin;
		result.pluginRouter = plugin.getPluginManager().getPluginRouter();
		result.kernelClass = plugin.getKernelClass();
		
		Patch uplink = AsyncPatch.create();
		result.sessionRouter.registerEndpoint(RequestRouter.UPSTREAM_NAME, uplink.getUpstream());
		result.pluginRouter.registerEndpoint("NetworkTraffic", uplink.getDownstream());
		
		result.registerManagerCallbacks();
		result.registerReflectCallbacks();
		
		plugin.getDefaultRequestHandler().attachRequestCallback(null, result);
		return result;
	}
	
	public static SessionManager create(Service service)
	{
		SessionManager result = new SessionManager();
		
		result.kernelClass = service.getSession().getKernel().getClass();
		result.sessions.put(service.getSession().getSessionUUID().toString(), service.getSession());
		
		result.registerManagerCallbacks();
		result.registerReflectCallbacks();

		service.getMycelium().addPersistentCallback(result);
		return result;
	}
	
	private void registerManagerCallbacks()
	{
		if (serverSide)
			handler.attachRequestCallback("CreateSession", ResponderCallback.create((s, req) ->
			{
				Session session = Session.create(plugin, req);
				String uuid = req.getHeader().getString("session");
				log.info("Session initiated for '" + uuid + "'");
				
				synchronized (sessions)
				{
					sessions.put(uuid, session);
				}
				
				return true;
			}));
	}
	
	private Session getSession(Request request)
	{
		Session session = null;
		
		if (request.getHeader().has("session"))
		{
			String uuid = request.getHeader().getString("session");
			
			synchronized (sessions)
			{
				if (sessions.containsKey(uuid))
				{
					session = sessions.get(uuid);
					
					if (serverSide)
					{
						String tok = request.getHeader().getString("token");
						request.getHeader().remove("token");
						
						if (!session.getToken().equals(tok))
						{
							log.error("'" + session.getSessionUUID().toString() + "' sent request with invalid token");
							session = null;
						}
					} else
					{
						//TODO: Consider client-side authentication
					}
				}
			}
		}
		
		return session;
	}
	
	private Object[] createMethodParam(Class<?>[] paramTypes, Request request, Session session, StitchLink source)
	{
		Object[] param = new Object[paramTypes.length];
		

		for (int i = 0; i < paramTypes.length; i ++)
		{
			if (paramTypes[i].equals(Request.class))
				param[i] = request;
			else if (paramTypes[i].equals(Session.class))
				param[i] = session;
			else if (paramTypes[i].equals(StitchLink.class))
				param[i] = source;
		}
		
		return param;
	}
	
	private void registerReflectCallbacks()
	{
		for (Method m : kernelClass.getDeclaredMethods())
		{
			Handler[] handled = m.getAnnotationsByType(Handler.class);
			
			for (Handler e : handled)
			{
				log.debug("'" + e.value() + "' <--> '" + m.getName() + "'");
				
				handler.attachRequestCallback(e.value(), ResponderCallback.create((source, request) ->
				{
					try
					{
						Session session = getSession(request);
						if (session == null)
							log.error("Sessionless connection attempted to invoke");
						else
							if (session.getKernel() != null)
							{
								Object result = m.invoke(session.getKernel(),
										createMethodParam(m.getParameterTypes(), request, session, source));
								if (result != null)
									if (result instanceof Boolean && serverSide)
										return (Boolean) result;
							}
						
						return false;
					} catch (Throwable t)
					{
						log.error("Failed to invoke session plugin method", t);
						return false;
					}
				}));
			}
		}
	}
	
	@Override
	public void onRequest(StitchLink source, Request request)
	{
		handler.onRequest(source, request);
	}
	
	public RequestRouter getSessionRouter()  { return sessionRouter; }
}
