package net.jibini.mycelium.hooks;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.jibini.mycelium.hook.Hook;
import net.jibini.mycelium.hook.Hooks;

public class TestHooks
{
	public static boolean type0Called = false;
	public static boolean type1Called = false;
	
	public static class HookedObject
	{
		@Hook("Type0")
		public void eventType0()
		{
			type0Called = true;
		}
		
		@Hook("Type1")
		public void eventType1(String arg)
		{
			type1Called = true;
			assertEquals(arg, "Hello, world!");
		}
	}
	
	@Test
	public void testHookNoArgs()
	{
		type0Called = type1Called = false;
		new Hooks()
				.registerHooks(new HookedObject())
				.callHooks("Type0");
		assertEquals(true, type0Called);
		assertEquals(false, type1Called);
	}
	
	@Test
	public void testHookWithArgs()
	{
		type0Called = type1Called = false;
		new Hooks()
				.registerHooks(new HookedObject())
				.callHooks("Type1", "Hello, world!");
		assertEquals(false, type0Called);
		assertEquals(true, type1Called);
	}
}
