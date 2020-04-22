package net.jibini.mycelium.link.patch;

import net.jibini.mycelium.link.Link;
import net.jibini.mycelium.link.tube.Tube;

public interface Patch<T> extends Link<T>
{
	Link<T> uplink();
	
	Tube<T> up();
	
	Tube<T> down();
}
