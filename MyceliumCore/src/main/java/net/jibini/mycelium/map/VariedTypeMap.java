package net.jibini.mycelium.map;

import org.json.JSONArray;
import org.json.JSONObject;

public interface VariedTypeMap<K> extends DirectMap<K, Object>
{
	String valueString(K key);
	
	boolean valueBoolean(K key);

	int valueInt(K key);

	float valueFloat(K key);

	double valueDouble(K key);
	
	JSONObject valueJSONObject(K key);
	
	JSONArray valueJSONArray(K key);
}
