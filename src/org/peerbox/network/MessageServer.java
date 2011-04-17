package org.peerbox.network;

public interface MessageServer {

	void setListener(MessageListener newListener);

	MessageSender getSender();

	void start();

}
