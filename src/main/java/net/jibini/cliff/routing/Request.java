package net.jibini.cliff.routing;

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
	
	public static Request create(JSONObject header, JSONObject body)
	{
		Request result = new Request();
		result.put("header", header);
		result.put("body", body);
		return result;
	}
	
	public static Request create(String target, String request, JSONObject body)
	{
		JSONObject header = new JSONObject();
		header.put("target", target);
		header.put("request", request);
		return create(header, body);
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
}
