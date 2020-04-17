package net.jibini.mycelium.api;

import net.jibini.mycelium.routing.StitchLink;

@Deprecated
@FunctionalInterface
public interface Responder
{
	boolean respond(StitchLink source, Request request);
}
