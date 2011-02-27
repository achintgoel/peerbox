package kademlia.messages;

import kademlia.Node;

public class FindNodeResponse extends FindResponse {
	protected Node foundNode;
	
	public Node getFoundNode() {
		return foundNode;
	}
}
