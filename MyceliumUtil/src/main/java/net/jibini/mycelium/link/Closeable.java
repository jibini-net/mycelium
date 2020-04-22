package net.jibini.mycelium.link;

public interface Closeable
{
	boolean isAlive();
	
	Closeable close();
}
