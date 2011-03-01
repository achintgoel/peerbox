package kademlia.messages;

import kademlia.Identifier;

public class FindNodeRequest extends FindRequest {
	protected Identifier targetIdentifier;
	final static public String command = "find_node";
	
	public FindNodeRequest(Identifier myNodeId, Identifier targetIdentifier) {
		this.myNodeId = myNodeId;
		this.targetIdentifier = targetIdentifier;
	}
	
	@Override
	public Identifier getTargetIdentifier() {
		return targetIdentifier;
	}

	@Override
	public String getCommand() {
		return command;
	}
}
