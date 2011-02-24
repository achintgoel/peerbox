package kademlia.messages;

import kademlia.Key;

public class StoreRequest extends Request {
	protected Key key;
	protected Message value; 
	final protected String command = "STORE";
	
}
