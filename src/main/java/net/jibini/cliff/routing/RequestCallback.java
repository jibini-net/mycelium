package net.jibini.cliff.routing;

@FunctionalInterface
public interface RequestCallback
{
	void onRequest(StitchLink source, Request request);
}
