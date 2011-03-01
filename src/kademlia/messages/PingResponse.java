package kademlia.messages;

import kademlia.Identifier;

public class PingResponse extends Response {
	protected Identifier myNodeId;
	
	public PingResponse(Identifier localNodeIdentifier) {
		this.myNodeId = localNodeIdentifier;
	}

	public Identifier getMyNodeId(){
		return myNodeId;
	}
}
