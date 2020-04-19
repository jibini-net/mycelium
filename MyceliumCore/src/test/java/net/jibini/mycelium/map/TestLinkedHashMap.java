package net.jibini.mycelium.map;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.jibini.mycelium.map.impl.LinkedElement;
import net.jibini.mycelium.map.impl.LinkedHashMap;

public class TestLinkedHashMap
{
	@Test
	public void testSimpleMutable()
	{
		assertEquals("Hello, world!", new LinkedHashMap<Integer, String>()
				.withMutableIndices()
				.insert(0, "Hello, world!")
				.insert(1, "Foo Bar")
				.value(0));
	}
	
	@Test
	public void testSimpleMutableInsert()
	{
		assertEquals("Foo Bar", new LinkedHashMap<Integer, String>()
				.withMutableIndices()
				.insert(0, "Hello, world!")
				.insert(0, "Foo Bar")
				.value(0));
	}
	
	@Test
	public void testSimpleGap()
	{
		assertEquals("Bar", new LinkedHashMap<Integer, String>()
				.insert(0, "Hello, world!")
				.insert(100, "Foo")
				.insert(200, "Bar")
				.value(200));
	}
	
	@Test
	public void testGapWithInsert()
	{
		assertEquals("Bar", new LinkedHashMap<Integer, String>()
				.insert(0, "Hello, world!")
				.insert(100, "Foo")
				.insert(50, "Bar")
				.value(50));
	}
	
	@Test
	public void testGapWithPreFirst()
	{
		assertEquals("Foo", new LinkedHashMap<Integer, String>()
				.insert(50, "Hello, world!")
				.insert(10, "Foo")
				.value(10));
	}
	
	@Test
	public void testStringKey()
	{
		assertEquals("Bar", new LinkedHashMap<String, String>()
				.insert("test", "Hello, world!")
				.insert("Foo", "Bar")
				.value("Foo"));
	}
	
	@Test
	public void testMutableBackToBack()
	{
		assertEquals("Bar", new LinkedHashMap<Integer, String>()
				.withMutableIndices()
				.insert(0, "Hello, world!")
				.insert(2, "Foo")
				.insert(1, "Bar")
				.value(1));
	}
	
	@Test
	public void testBackToBackPreFirst()
	{
		assertEquals("Bar", new LinkedHashMap<Integer, String>()
				.withMutableIndices()
				.insert(1, "Hello, world!")
				.insert(2, "Foo")
				.insert(0, "Bar")
				.value(0));
	}
	
	@Test
	public void testMutableChunkOverrun()
	{
		assertEquals("World", new LinkedHashMap<Integer, String>()
				.withMutableIndices()
				.insert(1, "Hello, world!")
				.insert(3, "Foo")
				.insert(2, "Hello")
				.insert(3, "World")
				.value(3));
	}
	
	@Test(expected=RuntimeException.class)
	public void testValueNotFound()
	{
		new LinkedHashMap<Integer, String>()
				.withMutableIndices()
				.insert(0, "Hello, world!")
				.value(100);
	}
	
	@Test(expected=RuntimeException.class)
	public void testValueNotFoundChunkClaim()
	{
		new LinkedHashMap<Integer, String>()
				.withMutableIndices()
				.insert(0, "Hello, world!")
				.value(1);
	}
	
	@Test(expected=RuntimeException.class)
	public void testValueNotFoundChunk()
	{
		new LinkedElement<Integer, String>()
				.withIndex(0)
				.put(0, new KeyValuePair<Integer, String>()
						.withValue("Hello, world!"))
				.value(1, true);
	}
	
	@Test(expected=RuntimeException.class)
	public void testHasNoPrevious()
	{
		new LinkedElement<Integer, String>()
				.previous();
	}
	
	@Test(expected=RuntimeException.class)
	public void testHasNoNext()
	{
		new LinkedElement<Integer, String>()
				.next();
	}
	
	public void testValueChunkImmutable()
	{
		new LinkedElement<Integer, String>()
				.withIndex(0)
				.put(0, new KeyValuePair<Integer, String>()
						.withValue("Hello, world!"))
				.value(1, false);
	}
	
	@Test(expected=RuntimeException.class)
	public void testZeroSizeError()
	{
		new LinkedHashMap<Integer, String>()
				.withMutableIndices()
				.value(0);
	}
	
	@Test
	public void testNegativeIndex()
	{
		assertEquals("Hello, world!", new LinkedHashMap<Integer, String>()
				.withMutableIndices()
				.insert(-1, "Hello, world!")
				.value(-1));
	}

	@Test(expected=RuntimeException.class)
	public void testAppendImmutable()
	{
		new LinkedHashMap<Integer, String>()
				.append("Hello, world!");
	}

	@Test
	public void testMutableAppend()
	{
		LinkedHashMap<Integer, String> data = new LinkedHashMap<Integer, String>()
				.withMutableIndices()
				.append("Hello, world!")
				.append("Foo Bar");
		assertEquals("Hello, world!", data.value(0));
		assertEquals("Foo Bar", data.value(1));
	}

	@Test
	public void testMutableNonSequentialAppend()
	{
		LinkedHashMap<Integer, String> data = new LinkedHashMap<Integer, String>()
				.withMutableIndices()
				.insert(1, "Foo")
				.append("Hello, world!")
				.insert(20, "Bar")
				.append("Foo Bar");
		assertEquals("Hello, world!", data.value(2));
		assertEquals("Foo Bar", data.value(21));
	}

	@Test
	public void testMutableIterator()
	{
		LinkedHashMap<Integer, Integer> data = new LinkedHashMap<Integer, Integer>()
				.withMutableIndices()
				.append(0)
				.append(1)
				.append(2)
				.append(3)
				.append(4);
		int c = 0;
		for (Integer i : data.values())
			assertEquals(c ++, i.intValue());
		assertEquals(5, c);
	}

	@Test
	public void testIteratorSequential()
	{
		LinkedHashMap<Integer, Integer> data = new LinkedHashMap<Integer, Integer>()
				.insert(0, 0)
				.insert(1, 1)
				.insert(2, 2)
				.insert(3, 3)
				.insert(4, 4);
		int c = 0;
		for (Integer i : data.values())
			assertEquals(c ++, i.intValue());
		assertEquals(5, c);
	}

	@Test
	public void testMutableIteratorNonSequential()
	{
		LinkedHashMap<Integer, Integer> data = new LinkedHashMap<Integer, Integer>()
				.withMutableIndices()
				.insert(0, 0)
				.insert(1, 2)
				.insert(1, 1)
				.insert(4, 4)
				.insert(3, 3);
		int c = 0;
		for (Integer i : data.values())
			assertEquals(c ++, i.intValue());
		assertEquals(5, c);
	}

	@Test
	public void testIteratorInsertPreStart()
	{
		LinkedHashMap<Integer, Integer> data = new LinkedHashMap<Integer, Integer>()
				.insert(10, 1)
				.insert(0, 0)
				.insert(11, 2);
		int c = 0;
		for (Integer i : data.values())
			assertEquals(c ++, i.intValue());
		assertEquals(3, c);
	}

	@Test
	public void testIteratorNotReady()
	{
		LinkedHashMap<Integer, Integer> data = new LinkedHashMap<Integer, Integer>();
		int c = 0;
		for (@SuppressWarnings("unused") Integer i : data.values())
			throw new RuntimeException("Should have no elements to iterate");
		assertEquals(0, c);
	}

	@Test
	public void testKeyValueIterator()
	{
		LinkedHashMap<Integer, Integer> data = new LinkedHashMap<Integer, Integer>()
				.withMutableIndices()
				.insert(0, 0)
				.insert(1, 2)
				.insert(1, 1)
				.insert(4, 4)
				.insert(3, 3);
		int c = 0;
		for (KeyValuePair<Integer, Integer> i : data.iterable())
			assertEquals(c ++, i.value().intValue());
		assertEquals(5, c);
	}

	@Test(expected=RuntimeException.class)
	public void testMutableKeysNotReturned()
	{
		LinkedHashMap<Integer, Integer> data = new LinkedHashMap<Integer, Integer>()
				.withMutableIndices()
				.insert(0, 0)
				.insert(1, 2)
				.insert(1, 1)
				.insert(4, 4)
				.insert(3, 3);
		for (KeyValuePair<Integer, Integer> i : data.iterable())
			i.key();
	}
	
	@Test
	public void testImmutableIterator()
	{
		LinkedHashMap<Integer, Integer> data = new LinkedHashMap<Integer, Integer>()
				.insert(1, 1)
				.insert(2, 3)
				.insert(2, 2)
				.insert(0, 0)
				.insert(4, 5)
				.insert(4, 4)
				.insert(3, 3);
		int c = 0;
		
		for (KeyValuePair<Integer, Integer> i : data.iterable())
		{
			assertEquals(c, i.value().intValue());
			assertEquals(c ++, i.key().intValue());
		}
		
		assertEquals(5, c);
	}
	
	@Test
	public void testHasValue()
	{
		LinkedHashMap<String, String> data = new LinkedHashMap<String, String>()
				.insert("Hello", "world!")
				.insert("Foo", "Bar");
		assertEquals(false, data.hasValue("Foo"));
		assertEquals(true, data.hasValue("world!"));
	}
	
	@Test
	public void testHasKey()
	{
		LinkedHashMap<String, String> data = new LinkedHashMap<String, String>()
				.insert("Hello", "world!")
				.insert("Foo", "Bar");
		assertEquals(false, data.hasKey("world!"));
		assertEquals(true, data.hasKey("Foo"));
	}
}
