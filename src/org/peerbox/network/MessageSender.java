package org.peerbox.network;

import java.net.URI;

public interface MessageSender {

	URI getLocalURI();

	void sendData(URI destination, String data);

}
