package org.peerbox.kademlia.messages;

import org.peerbox.kademlia.Identifier;

public class PingResponse extends Response {
	protected Identifier myNodeId;
	
	protected PingResponse() {
		
	}
	
	public PingResponse(Identifier localNodeIdentifier) {
		this.myNodeId = localNodeIdentifier;
	}

	public Identifier getMyNodeId(){
		return myNodeId;
	}
}
