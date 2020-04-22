package net.jibini.mycelium.link;

public interface Tube<T> extends Closeable
{
	Tube<T> push(T value);
	
	T pull();
}
