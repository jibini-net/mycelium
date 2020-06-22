package net.jibini.mycelium.api;

public class InteractionException extends RuntimeException
{
	public InteractionException(String message) { super(message); }
	
	public InteractionException(String message, Throwable t) { super(message, t); }
	
	public InteractionException(Throwable t) { super(t); }
}
