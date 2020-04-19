package net.jibini.mycelium.route;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.jibini.mycelium.api.InternalRequest;
import net.jibini.mycelium.link.StitchPatch;

public class TestRequestSwitch
{
	@Test(timeout=1000)
	public void testFromAToB()
	{
		StitchPatch a = new StitchPatch()
				.withName("A");
		StitchPatch b = new StitchPatch()
				.withName("B");
		new RequestSwitch()
				.routeBy("target")
				.attach(a)
				.attach(b);
		
		InternalRequest req = new InternalRequest();
		req.header().put("target", "B");
		
		a.send(req);
		assertEquals("B", b.read().header().getString("target"));
	}

	@Test(timeout=1000)
	public void testFromAToBNonTarget()
	{
		StitchPatch a = new StitchPatch()
				.withName("A");
		StitchPatch b = new StitchPatch()
				.withName("B");
		new RequestSwitch()
				.routeBy("element")
				.attach(a)
				.attach(b);
		
		InternalRequest req = new InternalRequest();
		req.header().put("element", "B");
		
		a.send(req);
		assertEquals("B", b.read().header().getString("element"));
	}

	@Test(timeout=1000)
	public void testNoAttachmentForTarget() throws InterruptedException
	{
		StitchPatch a = new StitchPatch()
				.withName("Patch:NoAttachmentTest-A");
		StitchPatch b = new StitchPatch()
				.withName("Patch:NoAttachmentTest-B");
		new RequestSwitch()
				.routeBy("target")
				.attach(a)
				.attach(b);
		
		InternalRequest req = new InternalRequest();
		req.header().put("target", "C");
		
		a.send(req);
		Thread.sleep(100);
	}

	@Test(timeout=1000)
	public void testDefaultGateway()
	{
		StitchPatch a = new StitchPatch();
		StitchPatch b = new StitchPatch();
		new RequestSwitch()
				.routeBy("target")
				.attach(a)
				.withDefaultGateway(b);
		
		InternalRequest req = new InternalRequest();
		req.header().put("target", "C");
		
		a.send(req);
		assertEquals("C", b.read().header().getString("target"));
	}

	@Test(timeout=1000)
	public void testDeadPatchRemoval() throws InterruptedException
	{
		StitchPatch a = new StitchPatch();
		StitchPatch b = new StitchPatch();
		new RequestSwitch()
				.routeBy("target")
				.attach(a)
				.attach(b);
		
		a.close();
		b.close();
		Thread.sleep(100);
	}
}
