package net.jibini.mycelium.api;

import org.json.JSONObject;

import net.jibini.mycelium.json.DecoratedJSONBindings;
import net.jibini.mycelium.json.JSONObjectBindings;

@SuppressWarnings("unchecked")
public abstract class AbstractRequest<THIS extends AbstractRequest<?>>
		extends DecoratedJSONBindings<String> implements Request
{
	private JSONObjectBindings content = new JSONObjectBindings()
			.insert("header", new JSONObject())
			.insert("body", new JSONObject());
//			.insert("response", new JSONObject());
	
	public THIS from(JSONObjectBindings content)
	{
		this.content = content;
		return (THIS)this;
	}
	
	public THIS from(JSONObject object)
	{
		return from(new JSONObjectBindings()
				.from(object));
	}
	
	public THIS from(String contents) { return from(new JSONObject(contents)); }
	
	public THIS from(AbstractRequest<?> request) { return from(request.toString()); }
	
	public THIS withHeader(JSONObject header) { dataMap().insert("header", header); return (THIS)this; }
	
	public THIS withBody(JSONObject body) { dataMap().insert("body", body); return (THIS)this; }
	
//	public THIS withResponse(JSONObject response) { dataMap().insert("response", response); return this; }
	
	public THIS withTarget(String target) { header().put("target", target); return (THIS)this; }
	
	public THIS withRequest(String request) { header().put("request", request); return (THIS)this; }
	
	@Override
	public JSONObject header() { return dataMap().valueJSONObject("header"); }
	
	@Override
	public JSONObject body() { return dataMap().valueJSONObject("body"); }
	
//	public JSONObject response() { return dataMap().valueJSONObject("response"); }

	@Override
	public JSONObjectBindings dataMap() { return content; }
}
