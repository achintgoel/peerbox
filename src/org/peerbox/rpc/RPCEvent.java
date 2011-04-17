package org.peerbox.rpc;

import java.net.URI;

public interface RPCEvent {
	public void respond(String data);

	public String getDataString();

	public String getServiceName();

	public URI getSenderURI();
}
