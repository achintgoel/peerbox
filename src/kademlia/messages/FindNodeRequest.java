package kademlia.messages;

import kademlia.Identifier;

public class FindNodeRequest extends FindRequest {
	protected Identifier targetIdentifier;
	final static public String COMMAND = "find_node";
	final protected String command;
	
	public FindNodeRequest(Identifier myNodeId, Identifier targetIdentifier) {
		this.myNodeId = myNodeId;
		this.targetIdentifier = targetIdentifier;
		this.command = COMMAND;
	}
	
	@Override
	public Identifier getTargetIdentifier() {
		return targetIdentifier;
	}

	@Override
	public String getCommand() {
		return COMMAND;
	}
}
