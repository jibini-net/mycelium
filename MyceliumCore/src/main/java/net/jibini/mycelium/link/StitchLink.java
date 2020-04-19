package net.jibini.mycelium.link;

import net.jibini.mycelium.api.Request;

public interface StitchLink
{
	StitchLink send(Request request);
	
	Request read();
	
	boolean isAlive();
	
	StitchLink close();
}
