package kademlia.messages;

import kademlia.Identifier;

public class PingRequest extends Request {
	protected Identifier targetId;
	final static public String COMMAND = "ping";
	
	protected PingRequest() {
		
	}
	
	public PingRequest(Identifier myNodeId, Identifier targetId){
		this.myNodeId = myNodeId;
		this.targetId = targetId;
		this.command = COMMAND;
	}
	
	public Identifier getTargetId(){
		return targetId;
	}

}
