package net.jibini.cliff.api;

import net.jibini.cliff.routing.Request;
import net.jibini.cliff.routing.StitchLink;

@FunctionalInterface
public interface Responder
{
	boolean respond(StitchLink source, Request request);
}
