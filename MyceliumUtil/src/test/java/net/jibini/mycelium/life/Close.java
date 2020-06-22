package net.jibini.mycelium.life;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.jibini.mycelium.annotation.Ordered;
import net.jibini.mycelium.annotation.Property;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

@Ordered
public @interface Close
{
	@Property(Ordered.PROP_ORDER)
	int value() default 0;
}
