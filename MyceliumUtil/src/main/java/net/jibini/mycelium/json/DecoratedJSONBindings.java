package net.jibini.mycelium.json;

import org.json.JSONArray;
import org.json.JSONObject;

import net.jibini.mycelium.map.VariedTypeMap;

public abstract class DecoratedJSONBindings<K> implements VariedTypeMap<K>
{
	@Override
	public Object value(K key) { return dataMap().value(key); }
	
	@Override
	public String valueString(K key) { return dataMap().valueString(key); }

	@Override
	public boolean valueBoolean(K key) { return dataMap().valueBoolean(key); }

	@Override
	public int valueInt(K key) { return dataMap().valueInt(key); }

	@Override
	public float valueFloat(K key) { return dataMap().valueFloat(key); }

	@Override
	public double valueDouble(K key) { return dataMap().valueDouble(key); }

	@Override
	public JSONObject valueJSONObject(K key) { return dataMap().valueJSONObject(key); }

	@Override
	public JSONArray valueJSONArray(K key) { return dataMap().valueJSONArray(key); }
	
	@Override
	public String toString() { return dataMap().toString(); }
	
	public String toString(int indentFactor) { return dataMap().toString(indentFactor); }
	
	public abstract JSONBindings<K> dataMap();
}
