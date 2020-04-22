package net.jibini.mycelium.link;

import net.jibini.mycelium.api.Request;
import net.jibini.mycelium.route.NetworkMember;

public final class StitchPatch extends AbstractPatch<Request, StitchPatch>
		implements NetworkMember, StitchLink
{
	private Tube<Request> up = new LatchedTube<Request>();
	private Tube<Request> down = new LatchedTube<Request>();
	
	@Override
	public Tube<Request> up()
	{
		return up;
	}

	@Override
	public Tube<Request> down()
	{
		return down;
	}

	@Override
	public StitchLink link()
	{
		return StitchLink.from(uplink());
	}
}
