package net.jibini.mycelium.invoke;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.jibini.mycelium.life.Close;
import net.jibini.mycelium.life.Closeable;

public class TestClosingCalls
{
	private static boolean done = false;
	
	public static class TestObject implements Closeable
	{
		@Close
		public void doClose()
		{
			done = true;
		}
	}
	
	@Test
	public void testCloseCall()
	{
		try
			(
				TestObject obj = new TestObject()
			)
		{} catch (Exception ex)
		{
			throw new RuntimeException(ex);
		} finally
		{
			assertEquals(true, done);
		}
	}
}
