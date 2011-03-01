package kademlia.messages;

import kademlia.Identifier;

public class PingRequest extends Request {
	final protected Identifier targetId;
	final static public String command = "ping";
	
	public PingRequest(Identifier myNodeId, Identifier targetId){
		this.myNodeId = myNodeId;
		this.targetId = targetId;
	}
	
	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return command;
	}
	
	public Identifier getTargetId(){
		return targetId;
	}

}
