package kademlia.messages;

import java.util.List;

import kademlia.Node;

public class FindNodeResponse extends FindResponse {
	protected Node foundNode;
	protected List<Node> nearbyNodes;
}
