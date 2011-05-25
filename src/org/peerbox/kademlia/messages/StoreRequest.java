package org.peerbox.kademlia.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.peerbox.kademlia.Identifier;
import org.peerbox.kademlia.Key;
import org.peerbox.kademlia.Value;

public class StoreRequest extends Request {
	protected Key key;
	protected List<Value> value;
	final static public String COMMAND = "store";

	protected StoreRequest() {

	}
	
	public StoreRequest(Identifier myNodeID, Key key, Value value) {
		this(myNodeID, key, Arrays.asList(value));
	}

	public StoreRequest(Identifier myNodeID, Key key, List<Value> value) {
		this.myNodeId = myNodeID;
		this.key = key;
		this.value = value;
		this.command = COMMAND;
	}

	public Key getKey() {
		return key;
	}

	public List<Value> getValue() {
		return value;
	}
}
