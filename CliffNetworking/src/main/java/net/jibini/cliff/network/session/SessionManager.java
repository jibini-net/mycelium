package net.jibini.cliff.network.session;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.cliff.api.RequestHandler;
import net.jibini.cliff.api.ResponderCallback;
import net.jibini.cliff.plugin.AbstractCliffPlugin;
import net.jibini.cliff.routing.AsyncPatch;
import net.jibini.cliff.routing.Patch;
import net.jibini.cliff.routing.Request;
import net.jibini.cliff.routing.RequestCallback;
import net.jibini.cliff.routing.RequestRouter;
import net.jibini.cliff.routing.StitchLink;

public class SessionManager implements RequestCallback
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private AbstractCliffPlugin service;
	private RequestRouter pluginRouter;
	
	private RequestRouter sessionRouter = RequestRouter.create("target", true);
	private RequestHandler handler = RequestHandler.create();
	private Map<String, Session> sessions = new HashMap<>();

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Handler
	{
		String value();
	}
	
	private SessionManager()
	{}
	
	public static SessionManager create(AbstractCliffPlugin service)
	{
		SessionManager result = new SessionManager();
		result.service = service;
		result.pluginRouter = service.getPluginManager().getPluginRouter();
		
		Patch uplink = AsyncPatch.create();
		result.sessionRouter.registerEndpoint(RequestRouter.UPSTREAM_NAME, uplink.getUpstream());
		result.pluginRouter.registerEndpoint("NetworkTraffic", uplink.getDownstream());
		
		result.registerManagerCallbacks();
		result.registerReflectCallbacks();
		
		return result;
	}
	
	private void registerManagerCallbacks()
	{
		handler.attachRequestCallback("CreateSession", ResponderCallback.create((s, req) ->
		{
			Session session = Session.create(req);
			String uuid = req.getHeader().getString("session");
			log.info("Session initiated for '" + uuid + "'");
			
			synchronized (sessions)
			{
				sessions.put(uuid, session);
			}
			
			return true;
		}));
	}
	
	private Object[] createMethodParam(Class<?>[] paramTypes, Request request, StitchLink source)
	{
		Object[] param = new Object[paramTypes.length];
		Session session = null;
		
		if (request.getHeader().has("session"))
		{
			String uuid = request.getHeader().getString("session");
			
			synchronized (sessions)
			{
				if (sessions.containsKey(uuid))
				{
					session = sessions.get(uuid);
					String tok = request.getHeader().getString("token");
					request.getHeader().remove("token");
					
					if (!session.getToken().equals(tok))
					{
						log.error("'" + session.getSessionUUID().toString() + "' sent request with invalid token");
						session = null;
					}
				}
			}
		}

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
		for (Method m : service.getClass().getDeclaredMethods())
		{
			Handler[] handled = m.getAnnotationsByType(Handler.class);
			
			for (Handler e : handled)
			{
				if (m.getReturnType().isPrimitive() && m.getReturnType().getTypeName().equals("boolean"))
				{
					log.debug("'" + e.value() + "' <--> '" + m.getName() + "'");
					
					handler.attachRequestCallback(e.value(), ResponderCallback.create((source, request) ->
					{
						try
						{
							return (boolean) m.invoke(service,
									createMethodParam(m.getParameterTypes(), request, source));
						} catch (Throwable t)
						{
							log.error("Failed to invoke session plugin method", t);
							return false;
						}
					}));
				} else
				{
					log.debug("'" + e.value() + "' ---> '" + m.getName() + "'");
					
					handler.attachRequestCallback(e.value(), (source, request) ->
					{
						try
						{
							m.invoke(service, createMethodParam(m.getParameterTypes(), request, source));
						} catch (Throwable t)
						{
							log.error("Failed to invoke session plugin method", t);
						}
					});
				}
			}
		}
	}
	
	@Override
	public void onRequest(StitchLink source, Request request)
	{
		handler.onRequest(source, request);
	}
}
