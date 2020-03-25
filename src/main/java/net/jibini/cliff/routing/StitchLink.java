package net.jibini.cliff.routing;

public interface StitchLink
{
	void sendRequest(Request request);
	
	void readRequest(RequestCallback callback);
	
	void addPersistentCallback(RequestCallback callback);
	
	void close();
}
