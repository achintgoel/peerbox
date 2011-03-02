package kademlia.messages;

import kademlia.Identifier;
import kademlia.Key;

public class FindValueRequest extends FindRequest {
	protected Key key;
	final static public String COMMAND = "find_value";
	
	protected FindValueRequest() {
		
	}
	
	public FindValueRequest(Identifier myNodeId, Key key) {
		this.myNodeId = myNodeId;
		this.key = key;
		this.command = COMMAND;
	}
	
	public Key getKey() {
		return key;
	}
	
	@Override
	public Identifier getTargetIdentifier() {
		return key.getIdentifier();
	}
	
}
