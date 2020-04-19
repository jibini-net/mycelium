package net.jibini.mycelium.error;

public final class EndOfStackException extends RuntimeException
{
	public EndOfStackException(String message) { super(message); }
	
	public EndOfStackException(String message, Throwable t) { super(message, t); }
	
	public EndOfStackException(Throwable t) { super(t); }
}
