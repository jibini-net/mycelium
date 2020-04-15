package net.jibini.mycelium.map;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestLinkedHashes
{
	@Test
	public void testSimpleMutable()
	{
		assertEquals("Hello, world!", new LinkedHashes<Integer, String>()
				.withMutableIndices()
				.insert(0, "Hello, world!")
				.insert(1, "Foo Bar")
				.value(0));
	}
	
	@Test
	public void testSimpleMutableInsert()
	{
		assertEquals("Foo Bar", new LinkedHashes<Integer, String>()
				.withMutableIndices()
				.insert(0, "Hello, world!")
				.insert(0, "Foo Bar")
				.value(0));
	}
	
	@Test
	public void testSimpleGap()
	{
		assertEquals("Bar", new LinkedHashes<Integer, String>()
				.insert(0, "Hello, world!")
				.insert(100, "Foo")
				.insert(200, "Bar")
				.value(200));
	}
	
	@Test
	public void testGapWithInsert()
	{
		assertEquals("Bar", new LinkedHashes<Integer, String>()
				.insert(0, "Hello, world!")
				.insert(100, "Foo")
				.insert(50, "Bar")
				.value(50));
	}
	
	@Test
	public void testGapWithPreFirst()
	{
		assertEquals("Foo", new LinkedHashes<Integer, String>()
				.insert(50, "Hello, world!")
				.insert(10, "Foo")
				.value(10));
	}
	
	@Test
	public void testStringKey()
	{
		assertEquals("Bar", new LinkedHashes<String, String>()
				.insert("test", "Hello, world!")
				.insert("Foo", "Bar")
				.value("Foo"));
	}
	
	@Test
	public void testMutableBackToBack()
	{
		assertEquals("Bar", new LinkedHashes<Integer, String>()
				.withMutableIndices()
				.insert(0, "Hello, world!")
				.insert(2, "Foo")
				.insert(1, "Bar")
				.value(1));
	}
	
	@Test
	public void testBackToBackPreFirst()
	{
		assertEquals("Bar", new LinkedHashes<Integer, String>()
				.withMutableIndices()
				.insert(1, "Hello, world!")
				.insert(2, "Foo")
				.insert(0, "Bar")
				.value(0));
	}
	
	@Test
	public void testMutableChunkOverrun()
	{
		assertEquals("World", new LinkedHashes<Integer, String>()
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
		new LinkedHashes<Integer, String>()
				.withMutableIndices()
				.insert(0, "Hello, world!")
				.value(100);
	}
	
	@Test(expected=RuntimeException.class)
	public void testValueNotFoundChunkClaim()
	{
		new LinkedHashes<Integer, String>()
				.withMutableIndices()
				.insert(0, "Hello, world!")
				.value(1);
	}
	
	@Test(expected=RuntimeException.class)
	public void testValueNotFoundChunk()
	{
		new LinkedElement<Integer, String>()
				.withIndex(0)
				.put(0, "Hello, world!")
				.value(1, true);
	}
	
	public void testValueChunkImmutable()
	{
		new LinkedElement<Integer, String>()
				.withIndex(0)
				.put(0, "Hello, world!")
				.value(1, false);
	}
	
	@Test(expected=RuntimeException.class)
	public void testZeroSizeError()
	{
		new LinkedHashes<Integer, String>()
				.withMutableIndices()
				.value(0);
	}
}
