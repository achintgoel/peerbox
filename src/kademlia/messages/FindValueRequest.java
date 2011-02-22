package kademlia.messages;

import kademlia.Key;

public class FindValueRequest extends FindRequest {
	Key key;
	final protected String command = "FIND_VALUE";
	
	@Override
	public String toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

}
