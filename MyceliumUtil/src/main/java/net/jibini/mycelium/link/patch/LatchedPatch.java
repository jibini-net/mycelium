package net.jibini.mycelium.link.patch;

import net.jibini.mycelium.link.tube.LatchedTube;
import net.jibini.mycelium.link.tube.Tube;

public class LatchedPatch<T> extends AbstractPatch<T, LatchedPatch<T>>
{
	private Tube<T> up = new LatchedTube<T>();
	private Tube<T> down = new LatchedTube<T>();
	
	@Override
	public Tube<T> up()
	{ return up; }

	@Override
	public Tube<T> down()
	{ return down; }
}
