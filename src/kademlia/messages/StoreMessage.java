package kademlia.messages;

import kademlia.Key;

public class StoreMessage implements Message {
	protected Key key;
	protected Message value; 
	final protected String command = "STORE";
	
	
	@Override
	public String toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

}
