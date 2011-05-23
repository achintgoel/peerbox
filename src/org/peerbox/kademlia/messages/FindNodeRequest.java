package org.peerbox.kademlia.messages;

import org.peerbox.kademlia.Identifier;

public class FindNodeRequest extends FindRequest {
	protected Identifier targetIdentifier;
	final static public String COMMAND = "find_node";

	protected FindNodeRequest() {
	}

	public FindNodeRequest(Identifier myNodeId, Identifier targetIdentifier) {
		this.myNodeId = myNodeId;
		this.targetIdentifier = targetIdentifier;
		this.command = COMMAND;
	}

	@Override
	public Identifier getTargetIdentifier() {
		return targetIdentifier;
	}

}
