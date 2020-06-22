package net.jibini.mycelium.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.jibini.mycelium.invoke.MethodCall;
import net.jibini.mycelium.invoke.MethodCallException;

public class AnnotationProperties
{
	private final Map<Object, Object> properties = new ConcurrentHashMap<>();
	private final Annotation ann;
	
	public AnnotationProperties(Annotation ann) { this.ann = ann; }
	
	
	public AnnotationProperties discoverProperties()
	{
		for (Method propValue : new MethodCall<Object>()
				.findAnnotatedMethods(Property.class, ann.annotationType()))
		{
			Property[] properties = propValue.getAnnotationsByType(Property.class);
			Object value;
			
			if (properties.length > 0)
			{
				try
				{
					value = propValue.invoke(ann);
				} catch (Throwable t)
				{
					throw new MethodCallException("Could not call property method", t);
				}
				
				for (Property p : properties)
					this.properties.put(p.value(), value);
			}
		}
		
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T value(Object key) { return (T)properties.get(key); }
	
	AnnotationProperties value(Object key, Object value) { properties.put(key, value); return this; }
	
	boolean has(Object key) { return properties.containsKey(key); }
}
