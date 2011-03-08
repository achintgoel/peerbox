package org.peerbox.kademlia.messages;

import org.peerbox.kademlia.Identifier;

public abstract class Request extends Message {
	protected Identifier myNodeId;
	protected String command;

	public Identifier getMyNodeId() {
		return myNodeId;
	}

	public String getCommand() {
		return command;
	}
}
