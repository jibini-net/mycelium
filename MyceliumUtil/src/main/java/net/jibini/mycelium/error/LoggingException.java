package net.jibini.mycelium.error;

public class LoggingException extends RuntimeException
{
	public LoggingException(String message) { super(message); }
	
	public LoggingException(String message, Throwable t) { super(message, t); }
	
	public LoggingException(Throwable t) { super(t); }
}
