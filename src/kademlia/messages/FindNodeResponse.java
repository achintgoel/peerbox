package kademlia.messages;

import java.util.List;

import kademlia.Node;

public class FindNodeResponse extends FindResponse {
	protected Node foundNode;
	
	protected FindNodeResponse() {
		
	}
	
	public FindNodeResponse(List<Node> nearestNodes) {
		this.foundNode = null;
		this.found = false;
		this.nearbyNodes = nearestNodes;
	}

	public FindNodeResponse(Node returnNode, List<Node> nearestNodes) {
		this.found = true;
		this.foundNode = returnNode;
		this.nearbyNodes = nearestNodes;
	}


	public Node getFoundNode() {
		return foundNode;
	}
}
