package net.jibini.mycelium.life;

import net.jibini.mycelium.invoke.MethodCall;

public interface Closeable extends AutoCloseable
{
	@Override
	public default void close() throws Exception
	{
		new MethodCall<Object>()
				.invokeAll(Close.class, this.getClass(), this, (result) ->
				{ });
	}
}
