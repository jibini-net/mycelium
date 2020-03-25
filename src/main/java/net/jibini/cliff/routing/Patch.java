package net.jibini.cliff.routing;

public interface Patch
{
	StitchLink getUpstream();
	
	StitchLink getDownstream();
	
	void close();
}
