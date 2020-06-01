package net.jibini.mycelium.api;

import net.jibini.mycelium.event.Event;
import net.jibini.mycelium.link.StitchLink;
import net.jibini.mycelium.resource.Checked;

public class RequestEvent implements Event
{
	private Checked<Request> request = new Checked<Request>()
			.withName("Request");
	private Checked<StitchLink> source = new Checked<StitchLink>()
			.withName("Request Source");
	
	public RequestEvent from(Request request)
	{ this.request.value(request); return this; }
	
	public RequestEvent withSource(StitchLink source)
	{ this.source.value(source); return this; }
	
	
	@Override
	public String type()
	{ return request.value().header().getString("request"); }

	@Override
	public String parentSpawnableName()
	{ return request.value().header().getString("interaction"); }
	
	
	public StitchLink source()
	{ return source.value(); }
	
	public Request request()
	{ return request.value(); }
	
	
	public RequestEvent respond(Request response)
	{ source().send(response); return this; }
	
	public RequestEvent echo()
	{ return respond(request()); }
}
