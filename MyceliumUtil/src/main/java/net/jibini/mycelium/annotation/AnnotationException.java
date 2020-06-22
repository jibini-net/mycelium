package net.jibini.mycelium.annotation;

public class AnnotationException extends RuntimeException
{
	public AnnotationException(String message) { super(message); }
	
	public AnnotationException(String message, Throwable t) { super(message, t); }
	
	public AnnotationException(Throwable t) { super(t); }
}
