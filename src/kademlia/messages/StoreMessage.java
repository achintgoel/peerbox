package kademlia.messages;

import kademlia.Key;

public class StoreMessage extends Message {
	protected Key key;
	protected Message value; 
	final protected String command = "STORE";
	
}
