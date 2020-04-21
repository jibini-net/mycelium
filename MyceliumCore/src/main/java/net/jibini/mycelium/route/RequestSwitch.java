package net.jibini.mycelium.route;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jibini.mycelium.api.InternalRequest;
import net.jibini.mycelium.api.Request;
import net.jibini.mycelium.error.MissingResourceException;
import net.jibini.mycelium.error.RoutingException;
import net.jibini.mycelium.resource.Checked;
import net.jibini.mycelium.thread.NamedThread;

public final class RequestSwitch implements Switch<RequestSwitch>
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private UUID uuid = UUID.randomUUID();
	private Map<String, NetworkMember> attached = new ConcurrentHashMap<>();
	private String headerElement = "target";
	
	private Checked<NetworkMember> defaultGateway = new Checked<NetworkMember>()
			.withName("Default Gateway");
	private Map<String, JSONObject> targetRoutes = new ConcurrentHashMap<>();
	
	private void routerLoop(NetworkMember member)
	{
		try
		{
			Request request = new InternalRequest().from(member.link().read());
			JSONObject route = request.header().getJSONObject("route");
			
			boolean hasExisting = route.has(uuid.toString());
			String existing = hasExisting ? route.getString(uuid.toString()) : "";
			route.put(uuid.toString(), member.address());
			
			if (hasExisting)
				attached.get(existing).link().send(request);
			else
			{
				String target = request.header().getString(headerElement);
				
				if (attached.containsKey(target))
					attached.get(target).link().send(request);
				else if (targetRoutes.containsKey(target))
				{
					JSONObject routeAdds = targetRoutes.get(target);
					for (String key : routeAdds.keySet())
						route.put(key, routeAdds.get(key));
					
					target = route.getString(uuid.toString());
					route.put(uuid.toString(), member.address());
					attached.get(target).link().send(request);
				} else
					try
					{
						log.debug("No attachment found, resorting to default gateway");
						defaultGateway().link().send(request);
					} catch (MissingResourceException ex)
					{
						throw new RoutingException("No attachment found for target '" + target + "'", ex);
					}
			}
		} catch (Throwable t)
		{
			if (System.getProperties().getOrDefault("verboseNetworking", false).equals("true"))
				log.warn("Error in routing request", t);
		}
	}
	
	public RequestSwitch withDefaultGateway(NetworkMember gateway)
	{
		this.defaultGateway.value(gateway);
		return this;
	}
	
	public RequestSwitch staticRoute(String target, JSONObject route) { this.targetRoutes.put(target, route); return this; }
	
	public RequestSwitch routeBy(String headerElement) { this.headerElement = headerElement; return this; }
	
	
	@Override
	public RequestSwitch attach(NetworkMember member)
	{
		attached.put(member.address(), member);
		new NamedThread()
				.withName("RouterThread:" + member.address())
				.asDaemon()
				.withRunnable(() ->
				{
					log.debug("Opened new switch connection");
					
					while (member.link().isAlive())
					{
						routerLoop(member);
						Thread.yield();
					}

					log.debug("Switch connection died, removing");
					attached.remove(member.address());
				})
				.start();
		return this;
	}
	
	@Override
	public NetworkMember defaultGateway() { return defaultGateway.value(); }
}
