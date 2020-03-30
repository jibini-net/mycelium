package net.jibini.cliff.routing;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestStitchLink
{
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private int read = 0;
	private Throwable thrown = null;
	
	@Test
	public void testAsyncTube() throws InterruptedException
	{
		StitchLink link = AsyncTube.create();
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 0 }")));
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 1 }")));
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 2 }")));
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 3 }")));
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 4 }")));
		
		RequestCallback callback = (l, r) ->
		{
			try
			{
				assertEquals(read++, r.getBody().getInt("value"));
			} catch (Throwable thrown)
			{
				this.thrown = thrown;
			}
		};
		
		for (int i = 0; i < 5; i ++)
			link.readRequest(callback);

		Thread.sleep(200);
		if (thrown != null)
			throw new RuntimeException(thrown);
		link.close();
	}
	
	@Test
	public void testAsyncWaiting() throws InterruptedException
	{
		StitchLink link = AsyncTube.create();
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 0 }")));
		Thread.sleep(20);
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 1 }")));
		Thread.sleep(20);
		
		RequestCallback callback = (l, r) ->
		{
			try
			{
				assertEquals(read++, r.getBody().getInt("value"));
			} catch (Throwable thrown)
			{
				this.thrown = thrown;
			}
		};
		
		for (int i = 0; i < 5; i ++)
		{
			link.readRequest(callback);
			Thread.sleep(20);
		}

		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 2 }")));
		Thread.sleep(20);
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 3 }")));
		Thread.sleep(20);
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 4 }")));

		Thread.sleep(200);
		if (thrown != null)
			throw new RuntimeException(thrown);
		assertEquals("Callback was not triggered 5 times", 5, read);
		link.close();
	}
	
	@Test
	public void testPersistentCallback() throws InterruptedException
	{
		StitchLink link = AsyncTube.create();
		
		RequestCallback callback = (l, r) ->
		{
			try
			{
				assertEquals(read++, r.getBody().getInt("value"));
			} catch (Throwable thrown)
			{
				this.thrown = thrown;
			}
		};
		
		link.addPersistentCallback(callback);
		
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 0 }")));
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 1 }")));
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 2 }")));
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 3 }")));
		link.sendRequest(Request.create("Test", "HelloWorld", new JSONObject("{ 'value': 4 }")));

		Thread.sleep(200);
		if (thrown != null)
			throw new RuntimeException(thrown);
		assertEquals("Callback was not triggered 5 times", 5, read);
		link.close();
	}
	
	@Test
	public void testAsyncPatch() throws InterruptedException
	{
		Patch hello = AsyncPatch.create();
		
		StitchLink helloDownstream = hello.getDownstream();
		StitchLink helloUpstream = hello.getUpstream();
		
		helloUpstream.addPersistentCallback((s, r) ->
		{
			log.debug("Upstream triggered");
			if (read == 0)
				assertEquals("World", r.getHeader().getString("request"));
			if (read == 1)
				assertEquals("Worl", r.getHeader().getString("request"));
			log.debug(r.toString());
			read++;
		});
		
		helloDownstream.sendRequest(Request.create("Hello", "World", new JSONObject()));
		Thread.sleep(40);
		helloDownstream.sendRequest(Request.create("Hell", "Worl", new JSONObject()));
		Thread.sleep(40);
		
		helloDownstream.addPersistentCallback((s, r) ->
		{
			log.debug("Downstream triggered");
			if (read == 2)
				assertEquals("NotWorld", r.getHeader().getString("request"));
			if (read == 4)
				log.debug(r.toString());
			read += 2;
		});
		
		helloUpstream.sendRequest(Request.create("NotHello", "NotWorld", new JSONObject()));
		Thread.sleep(40);
		helloUpstream.sendRequest(Request.create("NotHell", "NotWorl", new JSONObject()));
		Thread.sleep(40);
		
		assertEquals("Request callback did not trigger", 6, read);
		hello.close();
	}
}
