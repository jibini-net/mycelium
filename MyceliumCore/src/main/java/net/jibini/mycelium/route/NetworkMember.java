package net.jibini.mycelium.route;

import net.jibini.mycelium.link.Addressed;
import net.jibini.mycelium.link.StitchLink;

public interface NetworkMember extends Addressed
{
	StitchLink link();
}
