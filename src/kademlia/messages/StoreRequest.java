package kademlia.messages;

import kademlia.Identifier;
import kademlia.Key;

public class StoreRequest extends Request {
	protected Key key;
	protected String value; 
	final static public String command = "store";
	
	public StoreRequest(Identifier myNodeID, Key key, String value){
		this.myNodeId = myNodeID;
		this.key = key;
		this.value = value;
	}
	
	@Override
	public String getCommand() {
		return command;
	}
	
	public Key getKey(){
		return key;
	}
	
}
