package net.jibini.mycelium.invoke;

public class MethodCallException extends RuntimeException
{
	public MethodCallException(String message) { super(message); }
	
	public MethodCallException(String message, Throwable t) { super(message, t); }
	
	public MethodCallException(Throwable t) { super(t); }
}
