package net.jibini.mycelium.link.tube;

import net.jibini.mycelium.link.Closeable;

public interface Tube<T> extends Closeable
{
	Tube<T> push(T value);
	
	T pull();
}
