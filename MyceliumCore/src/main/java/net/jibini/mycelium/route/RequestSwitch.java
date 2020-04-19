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
import net.jibini.mycelium.thread.NamedThread;

public final class RequestSwitch implements Switch<RequestSwitch>
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private UUID uuid = UUID.randomUUID();
	private Map<String, NetworkMember> attached = new ConcurrentHashMap<>();
	private String headerElement = "target";
	
	private NetworkMember defaultGateway;
	private boolean hasDefaultGateway = false;
	
	private void routerLoop(NetworkMember member)
	{
		try
		{
			Request request = new InternalRequest().from(member.link().read());
			JSONObject route = request.header().getJSONObject("route");
			if (route.has(uuid.toString()))
				attached.get(route.get(uuid.toString())).link().send(request);
			else
			{
				route.put(uuid.toString(), member.address());
				String target = request.header().getString(headerElement);
				
				if (attached.containsKey(target))
					attached.get(target).link().send(request);
				else
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
			log.error("Error in routing request", t);
		}
	}
	
	public RequestSwitch withDefaultGateway(NetworkMember gateway)
	{
		this.defaultGateway = gateway;
		this.hasDefaultGateway = true;
		return this;
	}
	
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
	public NetworkMember defaultGateway()
	{
		if (hasDefaultGateway)
			return defaultGateway;
		else
			throw new MissingResourceException("Switch has no default gateway");
	}
}
