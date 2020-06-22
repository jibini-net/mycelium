package net.jibini.mycelium.event;

public class HandlerException extends RuntimeException
{
	public HandlerException(String message) { super(message); }
	
	public HandlerException(String message, Throwable t) { super(message, t); }
	
	public HandlerException(Throwable t) { super(t); }
}
