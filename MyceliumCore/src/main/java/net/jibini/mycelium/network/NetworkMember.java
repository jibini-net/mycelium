package net.jibini.mycelium.network;

import net.jibini.mycelium.link.Addressed;
import net.jibini.mycelium.link.StitchLink;

public interface NetworkMember extends Addressed
{
	StitchLink link();
}
