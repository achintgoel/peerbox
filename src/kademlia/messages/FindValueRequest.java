package kademlia.messages;

import kademlia.Identifier;
import kademlia.Key;

public class FindValueRequest extends FindRequest {
	protected Key key;
	final protected String command = "find_value";
	
	public FindValueRequest(Identifier myNodeId, Key key) {
		this.myNodeId = myNodeId;
		this.key = key;
	}
	
	@Override
	public Identifier getTargetIdentifier() {
		return key.getIdentifier();
	}

	@Override
	public String getCommand() {
		return command;
	}
	
}
