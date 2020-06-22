package net.jibini.mycelium.invoke;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.jibini.mycelium.annotation.AnnotationException;
import net.jibini.mycelium.annotation.AnnotationProperties;
import net.jibini.mycelium.annotation.Ordered;

public class MethodCall<T>
{
	@FunctionalInterface
	public static interface MethodCallback<T>
	{ void callback(T result); }
	
	@FunctionalInterface
	public static interface MethodCallable<T>
	{ void run(MethodCallback<T> callback, Object ... args); }
	

	public MethodCall<T> invokeAll(Class<? extends Annotation> annotation, Object instance, Object ... args)
	{
		genStatic(annotation, instance.getClass(), instance).run((result) -> { }, args);
		return this;
	}
	
	public MethodCall<T> invokeAll(Class<? extends Annotation> annotation, Class<?> instanceType,
			Object instance, Object ... args)
	{
		genStatic(annotation, instanceType, instance).run((result) -> { }, args);
		return this;
	}
	
	public MethodCall<T> invokeAll(Class<? extends Annotation> annotation, Class<?> instanceType,
			Object instance, MethodCallback<T> callback, Object ... args)
	{
		genStatic(annotation, instanceType, instance).run(callback, args);
		return this;
	}
	
	
	public MethodCallable<T> genStatic(Class<? extends Annotation> annotation, Object instance)
	{ return genStatic(annotation, instance.getClass(), instance); }
	
	
	@SuppressWarnings("unchecked")
	public MethodCallable<T> genStatic(List<Method> methods, Object instance)
	{
		return (callback, args) ->
		{
			for (Method m : methods)
				try
				{
					callback.callback((T)m.invoke(instance, args));
				} catch (Throwable t)
				{
					throw new MethodCallException("Error in method call", t);
				}
		};
	}
	
	public MethodCallable<T> genStatic(Class<? extends Annotation> annotation, Class<?> instanceType, Object instance)
	{
		List<Method> applicable = findAnnotatedMethods(annotation, instanceType);
		if (annotation.isAnnotationPresent(Ordered.class))
			sortAnnotatedMethods(annotation, applicable);
		return genStatic(applicable, instance);
	}
	
	
	public List<Method> findAnnotatedMethods(Class<? extends Annotation> annotation, Class<?> instanceType)
	{
		Method[] methods = instanceType.getMethods();
		List<Method> applicable = new ArrayList<Method>();
		for (Method m : methods)
			if (m.isAnnotationPresent(annotation))
				applicable.add(m);
		return applicable;
	}
	
	public List<Method> sortAnnotatedMethods(Class<? extends Annotation> annotation, List<Method> annotated)
	{
		Map<Method, AnnotationProperties> cache = new HashMap<>();
		
		annotated.sort((a, b) ->
		{
			try
			{
				if (!cache.containsKey(a))
					cache.put(a, new AnnotationProperties(a.getAnnotation(annotation))
							.discoverProperties());
				if (!cache.containsKey(b))
					cache.put(b, new AnnotationProperties(b.getAnnotation(annotation))
							.discoverProperties());
				
				int orderA = cache.get(a).<Integer>value(Ordered.PROP_ORDER);
				int orderB = cache.get(b).<Integer>value(Ordered.PROP_ORDER);
				
				return orderA - orderB;
			} catch (Throwable t)
			{
				throw new AnnotationException("Failed to determine method call indices", t);
			}
		});
		
		return annotated;
	}
}
