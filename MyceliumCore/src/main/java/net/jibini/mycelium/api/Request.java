package net.jibini.mycelium.api;

import org.json.JSONObject;

import net.jibini.mycelium.json.DecoratedJSONBindings;
import net.jibini.mycelium.json.JSONObjectBindings;

public final class Request extends DecoratedJSONBindings<String>
{
	private JSONObjectBindings content = new JSONObjectBindings()
			.insert("header", new JSONObject())
			.insert("body", new JSONObject())
			.insert("response", new JSONObject());
	
	public Request from(JSONObjectBindings content)
	{
		this.content = content;
		return this;
	}
	
	public Request from(JSONObject object)
	{
		return from(new JSONObjectBindings()
				.from(object));
	}
	
	public Request from(String contents) { return from(new JSONObject(contents)); }
	
	public Request from(Request request) { return from(request.toString()); }
	
	public Request withHeader(JSONObject header) { dataMap().insert("header", header); return this; }
	
	public Request withBody(JSONObject body) { dataMap().insert("body", body); return this; }
	
	public Request withResponse(JSONObject response) { dataMap().insert("response", response); return this; }
	
	public Request withTarget(String target) { header().put("target", target); return this; }
	
	public Request withRequest(String request) { header().put("request", request); return this; }
	
	
	public JSONObject header() { return dataMap().valueJSONObject("header"); }
	
	public JSONObject body() { return dataMap().valueJSONObject("body"); }
	
	public JSONObject response() { return dataMap().valueJSONObject("response"); }

	@Override
	public JSONObjectBindings dataMap() { return content; }
}
