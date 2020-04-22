package net.jibini.mycelium.link;

import net.jibini.mycelium.api.Request;
import net.jibini.mycelium.link.patch.LatchedPatch;
import net.jibini.mycelium.link.patch.Patch;
import net.jibini.mycelium.link.tube.Tube;
import net.jibini.mycelium.route.NetworkMember;

public class StitchPatch extends AbstractAddressed<StitchPatch>
		implements NetworkMember, StitchLink, Patch<Request>
{
	private Patch<Request> patch = new LatchedPatch<Request>();

	@Override
	public StitchLink link() { return StitchLink.from(patch.uplink()); }

	@Override
	public StitchPatch send(Request value)
	{
		patch.send(value);
		return this;
	}

	@Override
	public Request read() { return patch.read(); }
	

	@Override
	public StitchPatch close()
	{
		patch.close();
		return this;
	}

	@Override
	public boolean isAlive() { return patch.isAlive(); }

	@Override
	public Link<Request> uplink() { return patch.uplink(); }

	@Override
	public Tube<Request> up() { return patch.up(); }

	@Override
	public Tube<Request> down() { return patch.down(); }
}
