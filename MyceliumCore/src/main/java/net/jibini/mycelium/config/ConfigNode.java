package net.jibini.mycelium.config;

import org.json.JSONArray;
import org.json.JSONObject;

public interface ConfigNode<K, ParentType extends ConfigNode<?, ?>>
{
	ConfigNode<K, ParentType> value(K key, Object value);
	
	Object value(K key);

	String valueString(K key);
	
	boolean valueBoolean(K key);

	int valueInt(K key);

	float valueFloat(K key);

	double valueDouble(K key);
	
	JSONObject valueJSONObject(K key);
	
	JSONArray valueJSONArray(K key);
	
	boolean isOrphan();
	
	MapNode<? extends ConfigNode<K, ? extends ParentType>> pushNode(K key);
	
	ArrayNode<? extends ConfigNode<K, ? extends ParentType>> pushArray(K key);
	
	ParentType popNode();
}
