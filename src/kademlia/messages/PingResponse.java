package kademlia.messages;

import kademlia.Identifier;

public class PingResponse extends Response {
	protected Identifier myNodeId;
	
	public Identifier getMyNodeId(){
		return myNodeId;
	}
}
