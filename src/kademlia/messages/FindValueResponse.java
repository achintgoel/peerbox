package kademlia.messages;

import java.util.List;

import kademlia.Node;

public class FindValueResponse extends FindResponse {
	protected String foundValue;
	
	protected FindValueResponse() {
		
	}
	
	public FindValueResponse(List<Node> nearestNodes) {
		this.foundValue = null;
		this.found = false;
		this.nearbyNodes = nearestNodes;
	}
	
	public FindValueResponse(String foundValue, List<Node> nearestNodes) {
		this.foundValue = foundValue;
		this.found = true;
		this.nearbyNodes = nearestNodes;
	}
	
	public String getFoundValue() {
		return foundValue;
	}
}
