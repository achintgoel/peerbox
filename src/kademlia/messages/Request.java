package kademlia.messages;

import kademlia.Identifier;

public abstract class Request extends Message {
	public Identifier myNodeId;
	
	public abstract String getCommand();
	
	public Identifier getMyNodeId() {
		return myNodeId;
	}
}
