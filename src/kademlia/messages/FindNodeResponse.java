package kademlia.messages;

import java.util.List;

import kademlia.Node;

public class FindNodeResponse extends FindResponse {
	protected Node foundNode;
	
	public FindNodeResponse(List<Node> nearestNodes) {
		this.foundNode = null;
		this.found = false;
		this.nearbyNodes = nearestNodes;
	}

	public FindNodeResponse(Node returnNode) {
		this.found = true;
		this.foundNode = returnNode;
		this.nearbyNodes = null;
	}


	public Node getFoundNode() {
		return foundNode;
	}
}
