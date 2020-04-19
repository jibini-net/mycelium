package net.jibini.mycelium.link;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;

import net.jibini.mycelium.api.InternalRequest;

public class TestStitchPatch
{
	@Test(timeout=1000)
	public void testSendReceiveOutgoing()
	{
		InternalRequest req = new InternalRequest();
		req.body().put("value", "Hello, world!");
		
		assertEquals("Hello, world!", new StitchPatch()
					.send(req)
					.link().read()
					.body().getString("value"));
	}

	@Test(timeout=1000)
	public void testSendReceiveIncoming()
	{
		InternalRequest req = new InternalRequest();
		req.body().put("value", "Hello, world!");
		StitchPatch patch = new StitchPatch();
		
		patch.link().send(req);
		assertEquals("Hello, world!", patch
				.read()
					.body().getString("value"));
	}
	
	@Test(timeout=1000)
	public void testMultipleOutgoing()
	{
		InternalRequest req = new InternalRequest();
		StitchPatch patch = new StitchPatch();
		
		new Thread(() ->
		{
			int c = 0;
			patch.send(new InternalRequest().withBody(new JSONObject(req.body().put("value", c ++).toString())))
				.send(new InternalRequest().withBody(new JSONObject(req.body().put("value", c ++).toString())))
				.send(new InternalRequest().withBody(new JSONObject(req.body().put("value", c ++).toString())))
				.send(new InternalRequest().withBody(new JSONObject(req.body().put("value", c ++).toString())))
				.send(new InternalRequest().withBody(new JSONObject(req.body().put("value", c ++).toString())));
		}).start();
		
		for (int i = 0; i < 5; i ++)
			assertEquals(i, patch
					.link().read()
						.body().getInt("value"));
	}

	@Test(timeout=1000)
	public void testMultipleIncoming() throws InterruptedException
	{
		InternalRequest req = new InternalRequest();
		StitchPatch patch = new StitchPatch();
		
		new Thread(() ->
		{
			int c = 0;
			patch.link().send(new InternalRequest().withBody(new JSONObject(req.body().put("value", c ++).toString())))
				.send(new InternalRequest().withBody(new JSONObject(req.body().put("value", c ++).toString())))
				.send(new InternalRequest().withBody(new JSONObject(req.body().put("value", c ++).toString())))
				.send(new InternalRequest().withBody(new JSONObject(req.body().put("value", c ++).toString())))
				.send(new InternalRequest().withBody(new JSONObject(req.body().put("value", c ++).toString())));
		}).start();
		
		for (int i = 0; i < 5; i ++)
			assertEquals(i, patch
					.read()
						.body().getInt("value"));
	}
}
