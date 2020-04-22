package net.jibini.mycelium.link;

public interface Link<T> extends Closeable
{
	Link<T> send(T value);
	
	T read();
	
	Link<T> close();
}
