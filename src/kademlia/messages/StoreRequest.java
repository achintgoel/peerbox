package kademlia.messages;

import kademlia.Key;

public class StoreRequest extends Request {
	protected Key key;
	protected String value; 
	final protected String command = "store";
	
	@Override
	public String getCommand() {
		return command;
	}
	
}
