package net.jibini.mycelium.conf;

import net.jibini.mycelium.map.KeyValueMap;
import net.jibini.mycelium.map.LinkedHashMap;

public final class ConfigFile extends AbstractConfigNode<String, ConfigFile, ConfigFile>
{
	KeyValueMap<String, Object> dataMap = new LinkedHashMap<String, Object>();

	@Override
	public ConfigFile pop() { return this; }

	@Override
	public KeyValueMap<String, Object> dataMap() { return dataMap; }
}
