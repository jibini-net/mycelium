package net.jibini.cliff.api;

import net.jibini.cliff.routing.Request;

@FunctionalInterface
public interface Responder
{
	boolean respond(Request request);
}
