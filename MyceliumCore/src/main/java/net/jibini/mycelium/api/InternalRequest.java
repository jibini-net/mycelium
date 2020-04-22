package net.jibini.mycelium.api;

import org.json.JSONObject;

import net.jibini.mycelium.json.DecoratedJSONBindings;
import net.jibini.mycelium.json.JSONObjectBindings;

public class InternalRequest extends DecoratedJSONBindings<String>
		implements Request
{
	private JSONObjectBindings content = new JSONObjectBindings()
			.insert("header", new JSONObject()
					.put("route", new JSONObject()))
			.insert("body", new JSONObject());
//			.insert("response", new JSONObject());
	
	public InternalRequest from(JSONObjectBindings content)
	{
		this.content = content;
		return this;
	}
	
	public InternalRequest from(JSONObject object)
	{
		return from(new JSONObjectBindings()
				.from(object));
	}
	
	public InternalRequest from(String contents) { return from(new JSONObject(contents)); }
	
	public InternalRequest from(Request request) { return from(request.toString()); }
	
	public InternalRequest withHeader(JSONObject header) { dataMap().insert("header", header); return this; }
	
	public InternalRequest withHeader(String key, Object value) { header().put(key, value); return this; }
	
	public InternalRequest withBody(JSONObject body) { dataMap().insert("body", body); return this; }
	
//	public THIS withResponse(JSONObject response) { dataMap().insert("response", response); return this; }
	
	public InternalRequest withTarget(String target) { header().put("target", target); return this; }
	
	public InternalRequest withRequest(String request) { header().put("request", request); return this; }
	
	@Override
	public JSONObject header() { return dataMap().valueJSONObject("header"); }
	
	@Override
	public JSONObject body() { return dataMap().valueJSONObject("body"); }
	
//	public JSONObject response() { return dataMap().valueJSONObject("response"); }

	@Override
	public JSONObjectBindings dataMap() { return content; }
}
