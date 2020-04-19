package net.jibini.mycelium.error;

public final class InvalidOperationException extends RuntimeException
{
	public InvalidOperationException(String message) { super(message); }
	
	public InvalidOperationException(String message, Throwable t) { super(message, t); }
	
	public InvalidOperationException(Throwable t) { super(t); }
}
