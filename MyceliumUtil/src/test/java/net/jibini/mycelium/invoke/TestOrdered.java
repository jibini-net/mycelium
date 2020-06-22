package net.jibini.mycelium.invoke;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.jibini.mycelium.life.Close;

public class TestOrdered
{
	private int c = 0;
	
	@Test
	public void testUnordered()
	{
		new MethodCall<Object>().invokeAll(Close.class, this, false);
		assertEquals(3, c);
	}
	
	@Test
	public void testOrdered()
	{
		new MethodCall<Object>().invokeAll(Close.class, this, true);
		assertEquals(3, c);
	}
	
	private void closeOpGeneral(int num, boolean ordered)
	{
		if (ordered)
			assertEquals(num, c);
		c ++;
	}
	
	@Close(0)
	public void closeOp0(boolean ordered)
	{
		assertEquals(0, c);
		closeOpGeneral(0, ordered);
	}
	
	@Close(1)
	public void closeOp1(boolean ordered)
	{
		assertEquals(1, c);
		closeOpGeneral(1, ordered);
	}
	
	@Close(2)
	public void closeOp2(boolean ordered)
	{
		assertEquals(2, c);
		closeOpGeneral(2, ordered);
	}
}
