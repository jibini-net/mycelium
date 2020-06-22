package net.jibini.mycelium.resource;

public class MissingResourceException extends RuntimeException
{
	public MissingResourceException(String message) { super(message); }
	
	public MissingResourceException(String message, Throwable t) { super(message, t); }
	
	public MissingResourceException(Throwable t) { super(t); }
}
