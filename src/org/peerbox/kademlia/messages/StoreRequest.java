package org.peerbox.kademlia.messages;

import org.peerbox.kademlia.Identifier;
import org.peerbox.kademlia.Key;
import org.peerbox.kademlia.Value;

public class StoreRequest extends Request {
	protected Key key;
	protected Value value; 
	final static public String COMMAND = "store";
	
	protected StoreRequest() {
		
	}
	
	public StoreRequest(Identifier myNodeID, Key key, Value value){
		this.myNodeId = myNodeID;
		this.key = key;
		this.value = value;
		this.command = COMMAND;
	}
	
	public Key getKey(){
		return key;
	}
	
	public Value getValue() {
		return value;
	}
}
