package net.jibini.mycelium.routing;

import org.json.JSONArray;
import org.json.JSONObject;

public class Request extends JSONObject
{
	private Request()
	{
		super();
	}
	
	private Request(String contents)
	{
		super(contents);
	}
	
	public static Request create(String contents)
	{
		return new Request(contents);
	}
	
	public static Request create(Request contents)
	{
		return Request.create(contents.toString());
	}
	
	public static Request create(JSONObject header, JSONObject body, JSONObject response)
	{
		Request result = new Request();
		result.put("header", header);
		result.put("body", body);
		result.put("response", response);
		if (!header.has("route"))
			header.put("route", new JSONArray());
		return result;
	}
	
	public static Request create(JSONObject header, JSONObject body)
	{
		return create(header, body, new JSONObject());
	}
	
	public static Request create(String target, String request, JSONArray route, JSONObject body)
	{
		JSONObject header = new JSONObject();
		header.put("target", target);
		header.put("request", request);
		header.put("route", route);
		return create(header, body);
	}
	
	public static Request create(String target, String request, JSONObject body)
	{
		return create(target, request, new JSONArray(), body);
	}
	
	public static Request create(String target, String request)
	{
		return create(target, request, new JSONObject());
	}
	
	public JSONObject getHeader()
	{
		return getJSONObject("header");
	}
	
	public JSONObject getBody()
	{
		return getJSONObject("body");
	}
	
	public JSONObject getResponse()
	{
		return getJSONObject("response");
	}
}
