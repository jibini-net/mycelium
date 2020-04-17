package net.jibini.mycelium.map;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.jibini.mycelium.map.impl.LinkedArray;

public class TestLinkedArray
{
	@Test
	public void testIteratorKeysGenerated()
	{
		LinkedArray<Integer> array = new LinkedArray<Integer>()
				.insert(0, 0)
				.insert(1, 2)
				.insert(1, 1)
				.insert(4, 4)
				.insert(3, 3);
		int c = 0;
		
		for (KeyValuePair<Integer, Integer> i : array.iterable())
		{
			assertEquals(c, i.value().intValue());
			assertEquals(c ++, i.key().intValue());
		}
		
		assertEquals(5, c);
	}
	
	@Test
	public void testIndexReassignment()
	{
		LinkedArray<Integer> array = new LinkedArray<Integer>()
				.insert(new KeyValuePair<Integer, Integer>()
						.withKey(0)
						.withValue(4))
				.insert(0, 3)
				.insert(0, 2)
				.insert(0, 1)
				.insert(0, 0);
		assertEquals(4, array.value(4).intValue());
		assertEquals(4, array.keyValue(4).key().intValue());
	}
	
	@Test
	public void testAppendKeysGenerated()
	{
		LinkedArray<Integer> array = new LinkedArray<Integer>()
				.append(0)
				.append(1)
				.append(2)
				.append(3)
				.append(4);
		int c = 0;
		
		for (KeyValuePair<Integer, Integer> i : array.iterable())
		{
			assertEquals(c, i.value().intValue());
			assertEquals(c ++, i.key().intValue());
		}
		
		assertEquals(5, c);
	}
	
	@Test
	public void testAppendOrderKept()
	{
		LinkedArray<Integer> array = new LinkedArray<Integer>()
				.append(0)
				.append(3)
				.append(2)
				.append(6)
				.append(2);
		int c = 0;
		int[] expected = { 0, 3, 2, 6, 2 };
		
		for (Integer i : array.values())
			assertEquals(expected[c ++], i.intValue());
		assertEquals(5, c);
	}
	
	@Test
	public void testHasValue()
	{
		LinkedArray<Integer> array = new LinkedArray<Integer>()
				.append(0)
				.append(3)
				.append(2)
				.append(6)
				.append(2);
		assertEquals(false, array.hasValue(1));
		assertEquals(true, array.hasValue(0));
	}
	
	@Test
	public void testHasKey()
	{
		LinkedArray<Integer> array = new LinkedArray<Integer>()
				.append(0)
				.append(3)
				.append(2)
				.append(6)
				.append(2);
		assertEquals(false, array.hasKey(5));
		assertEquals(true, array.hasKey(4));
	}
}
