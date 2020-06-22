package net.jibini.mycelium;

import net.jibini.mycelium.spore.SporeProfile;

public class MyceliumProfile implements SporeProfile
{
	@Override
	public String serviceName()
	{ return "Mycelium"; }

	@Override
	public String version()
	{ return "1.0.0a-SNAPSHOT"; }

//	@Override
//	public int protocolVersion()
//	{ return 1; }
}
