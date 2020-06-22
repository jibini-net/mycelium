package net.jibini.mycelium.hook;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import net.jibini.mycelium.annotation.AnnotationProperties;
import net.jibini.mycelium.invoke.MethodCall;
import net.jibini.mycelium.invoke.MethodCallException;

public class Hooks
{
	private final Map<String, List<MethodCall.MethodCallable<?>>> hooks = new ConcurrentHashMap<>();
	
	public Hooks registerHooks(Object instance)
	{
		MethodCall<Object> call = new MethodCall<>();
		List<Method> hookAnnotated = call.findAnnotatedMethods(Hook.class, instance.getClass());
		
		for (Method hookMethod : hookAnnotated)
		{
			String hook = new AnnotationProperties(hookMethod.getAnnotation(Hook.class))
					.discoverProperties()
					.<String>value(Hook.PROP_HOOK);
			hooks.putIfAbsent(hook, new CopyOnWriteArrayList<>());
			
			hooks.get(hook)
					.add((callback, args) ->
					{
						try
						{
							hookMethod.invoke(instance, args);
						} catch (Throwable t)
						{
							throw new MethodCallException("Failed to invoke hooked method", t);
						}
					});
		}
		
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T> Hooks callHooks(String hook, MethodCall.MethodCallback<T> callback, Object ... args)
	{
		if (hooks.containsKey(hook))
			for (MethodCall.MethodCallable<?> callable : hooks.get(hook))
				((MethodCall.MethodCallable<T>)callable).run(callback, args);
		return this;
	}
	
	public Hooks callHooks(String hook, Object ... args)
	{
		if (hooks.containsKey(hook))
			for (MethodCall.MethodCallable<?> callable : hooks.get(hook))
				callable.run((result) -> { }, args);
		return this;
	}
}
