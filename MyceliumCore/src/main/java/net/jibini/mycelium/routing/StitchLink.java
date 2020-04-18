package net.jibini.mycelium.routing;

import java.io.Closeable;

import net.jibini.mycelium.api.ReceivedRequest;
import net.jibini.mycelium.api.Request;

public interface StitchLink extends Closeable
{
	void send(Request request);
	
	ReceivedRequest read();
	
	// TODO
//	BreakoutThread breakoutExchange(StitchLink link)
}
