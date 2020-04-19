package net.jibini.mycelium.api;

import org.json.JSONObject;

import net.jibini.mycelium.map.VariedTypeMap;

public interface Request extends VariedTypeMap<String>
{
	JSONObject header();
	
	JSONObject body();
	
	String toString(int indentFactor);
	
	String toString();
}
