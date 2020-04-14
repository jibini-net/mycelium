package net.jibini.mycelium.routing;

@FunctionalInterface
public interface RequestCallback
{
	void onRequest(StitchLink source, Request request);
}
