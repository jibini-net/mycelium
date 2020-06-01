package net.jibini.mycelium.api;

import org.json.JSONObject;

public class InternalRequest implements Request
{
	private JSONObject data = new JSONObject()
			.put("header", new JSONObject()
					.put("route", new JSONObject())
				)
			.put("body", new JSONObject());
	
	public InternalRequest from(JSONObject data)
	{ this.data = data; return this; }
	
	public InternalRequest from(String contents)
	{ return from(new JSONObject(contents)); }
	
	public InternalRequest from(Request request)
	{ return from(request.toString()); }
	
	public InternalRequest withHeader(JSONObject header)
	{ data.put("header", header); return this; }
	
	public InternalRequest withHeader(String key, Object value)
	{ header().put(key, value); return this; }
	
	public InternalRequest withBody(JSONObject body)
	{ data.put("body", body); return this; }
	
	public InternalRequest withTarget(String target)
	{ header().put("target", target); return this; }
	
	public InternalRequest withRequest(String request)
	{ header().put("request", request); return this; }
	
	
	@Override
	public JSONObject header() { return data.getJSONObject("header"); }
	
	@Override
	public JSONObject body() { return data.getJSONObject("body"); }

	@SuppressWarnings("unchecked")
	@Override
	public <T> T value(String key)
	{ return (T)data.get(key); }


	@Override
	public String toString()
	{ return data.toString(); }
	
	@Override
	public String toString(int indentFactor)
	{ return data.toString(indentFactor); }
}
