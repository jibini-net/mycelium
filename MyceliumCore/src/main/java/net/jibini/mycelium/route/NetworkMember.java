package net.jibini.mycelium.route;

import net.jibini.mycelium.link.StitchLink;

public interface NetworkMember
{
	String address();
	
	StitchLink link();
}
