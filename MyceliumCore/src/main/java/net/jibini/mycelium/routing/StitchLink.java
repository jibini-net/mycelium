package net.jibini.mycelium.routing;

import net.jibini.mycelium.api.Request;

public interface StitchLink
{
	void sendRequest(Request request);
	
	void readRequest(RequestCallback callback);
	
	void addPersistentCallback(RequestCallback callback);
	
	void close();
}
