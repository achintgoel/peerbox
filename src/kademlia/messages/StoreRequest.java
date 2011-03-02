package kademlia.messages;

import kademlia.Identifier;
import kademlia.Key;

public class StoreRequest extends Request {
	protected Key key;
	protected String value; 
	final static public String COMMAND = "store";
	
	protected StoreRequest() {
		
	}
	
	public StoreRequest(Identifier myNodeID, Key key, String value){
		this.myNodeId = myNodeID;
		this.key = key;
		this.value = value;
		this.command = COMMAND;
	}
	
	public Key getKey(){
		return key;
	}
	
	public String getValue() {
		return value;
	}
}
