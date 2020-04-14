package net.jibini.mycelium.api;

import net.jibini.mycelium.routing.Request;
import net.jibini.mycelium.routing.StitchLink;

@FunctionalInterface
public interface Responder
{
	boolean respond(StitchLink source, Request request);
}
