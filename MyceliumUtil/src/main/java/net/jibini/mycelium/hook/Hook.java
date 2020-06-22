package net.jibini.mycelium.hook;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.jibini.mycelium.annotation.Property;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Hook
{
	static final String PROP_HOOK = "hook";
	
	@Property(Hook.PROP_HOOK)
	String value();
}
