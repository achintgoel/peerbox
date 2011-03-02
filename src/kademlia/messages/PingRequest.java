package kademlia.messages;

import kademlia.Identifier;

public class PingRequest extends Request {
	final protected Identifier targetId;
	final static public String COMMAND = "ping";
	final protected String command;
	
	public PingRequest(Identifier myNodeId, Identifier targetId){
		this.myNodeId = myNodeId;
		this.targetId = targetId;
		this.command = COMMAND;
	}
	
	@Override
	public String getCommand() {
		return COMMAND;
	}
	
	public Identifier getTargetId(){
		return targetId;
	}

}
