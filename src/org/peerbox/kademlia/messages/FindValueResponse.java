package org.peerbox.kademlia.messages;

import java.util.LinkedList;
import java.util.List;

import org.peerbox.kademlia.Node;
import org.peerbox.kademlia.Value;


public class FindValueResponse extends FindResponse {
	protected List<Value> foundValue;
	
	protected FindValueResponse() {
		
	}
	
	public FindValueResponse(List<Node> nearestNodes) {
		this.foundValue = null;
		this.found = false;
		this.nearbyNodes = nearestNodes;
	}
	
	public FindValueResponse(List<Value> foundValue, List<Node> nearestNodes) {
		this.foundValue = foundValue;
		this.found = true;
		this.nearbyNodes = nearestNodes;
	}
	
	public List<Value> getFoundValue() {
		return foundValue;
	}
}
