package net.jibini.mycelium.route;

public interface Switch<THIS>
{
	THIS attach(NetworkMember member);
	
	NetworkMember defaultGateway();
}
