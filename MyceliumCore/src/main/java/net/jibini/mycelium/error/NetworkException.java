package net.jibini.mycelium.error;

public final class NetworkException extends RuntimeException
{
	public NetworkException(String message) { super(message); }
	
	public NetworkException(String message, Throwable t) { super(message, t); }
	
	public NetworkException(Throwable t) { super(t); }
}
