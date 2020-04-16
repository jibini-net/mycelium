package net.jibini.mycelium.routing;

import net.jibini.mycelium.api.Request;

@FunctionalInterface
public interface RequestCallback
{
	void onRequest(StitchLink source, Request request);
}
