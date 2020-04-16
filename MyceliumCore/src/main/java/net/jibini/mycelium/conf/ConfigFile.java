package net.jibini.mycelium.conf;

import net.jibini.mycelium.json.JSONObjectBindings;

public final class ConfigFile extends AbstractConfigNode<String, ConfigFile, ConfigFile>
{
	JSONObjectBindings dataMap = new JSONObjectBindings();

	@Override
	public ConfigFile pop() { return this; }

	@Override
	public JSONObjectBindings dataMap() { return dataMap; }
}
