package net.jibini.mycelium.routing;

public interface Patch
{
	StitchLink getUpstream();
	
	StitchLink getDownstream();
	
	void close();
}
