package net.jibini.mycelium.thread;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TestNamedThread
{
	private CountDownLatch latch = new CountDownLatch(1);
	
	private void await() throws InterruptedException
	{
		latch.await(1, TimeUnit.SECONDS);
		Thread.sleep(100);
	}
	
	@Test
	public void testNamedThread() throws Throwable
	{
		NamedThread thread = new NamedThread()
			.withName("Name")
			.withRunnable(() ->
			{
				assertEquals("Name", Thread.currentThread().getName());
			})
			.start();
		thread.checkException();
	}
	
	@Test(expected=RuntimeException.class)
	public void testCheckException() throws Throwable
	{
		NamedThread thread = new NamedThread()
			.withName("Name")
			.withRunnable(() ->
			{
				latch.countDown();
				throw new RuntimeException("Unchecked internal exception");
			})
			.start();
		
		await();
		thread.checkException();
	}
	
	@Test
	public void testIsAlive() throws Throwable
	{
		NamedThread thread = new NamedThread()
			.withName("Name")
			.withRunnable(() ->
			{
				try
				{
					Thread.sleep(100);
					latch.countDown();
				} catch (InterruptedException ex)
				{  }
			})
			.start();
		
		assertEquals(true, thread.isAlive());
		await();
		assertEquals(false, thread.isAlive());
		thread.checkException();
	}
	
	@Test(expected=RuntimeException.class)
	public void testInterrupt() throws Throwable
	{
		NamedThread thread = new NamedThread()
			.withName("Name")
			.asDaemon()
			.withRunnable(() ->
			{
				try
				{
					Thread.sleep(500);
				} catch (InterruptedException ex)
				{
					throw new RuntimeException(ex);
				}
			})
			.start();
		
		thread.interrupt();
		await();
		thread.checkException();
	}
}
