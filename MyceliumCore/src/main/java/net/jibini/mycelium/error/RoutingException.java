package net.jibini.mycelium.error;

public final class RoutingException extends RuntimeException
{
	public RoutingException(String message) { super(message); }
	
	public RoutingException(String message, Throwable t) { super(message, t); }
	
	public RoutingException(Throwable t) { super(t); }
}
