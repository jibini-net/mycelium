package net.jibini.cliff.api;

import net.jibini.cliff.routing.Request;

@FunctionalInterface
public interface Responder
{
	void respond(Request request, Request response);
}
