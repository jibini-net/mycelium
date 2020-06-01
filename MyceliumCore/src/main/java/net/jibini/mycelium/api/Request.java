package net.jibini.mycelium.api;

import org.json.JSONObject;

public interface Request
{
	JSONObject header();
	
	JSONObject body();
	
	<T> T value(String key);
	
	
	String toString(int indentFactor);
	
	@Override
	String toString();
}
