package org.peerbox.rpc;

import java.net.URI;

public interface RPCHandler {

	URI getLocalURI();

	void setLocalURI(URI uri);

	void registerServiceListener(String serviceName, RPCServiceRequestListener serviceListener);

	void sendRequest(URI recipient, String serviceName, String dataString, RPCResponseListener responseListener);

}
