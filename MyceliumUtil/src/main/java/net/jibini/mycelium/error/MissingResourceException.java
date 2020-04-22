package net.jibini.mycelium.error;

public final class MissingResourceException extends RuntimeException
{
	public MissingResourceException(String message) { super(message); }
	
	public MissingResourceException(String message, Throwable t) { super(message, t); }
	
	public MissingResourceException(Throwable t) { super(t); }
}
