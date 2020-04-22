package net.jibini.mycelium.link;

public interface Patch<T> extends Link<T>
{
	Link<T> uplink();
	
	Tube<T> up();
	
	Tube<T> down();
}
