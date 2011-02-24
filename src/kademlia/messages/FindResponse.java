package kademlia.messages;

import java.util.List;

import kademlia.Node;

public abstract class FindResponse extends Response {
	protected boolean found;
	protected List<Node> nearbyNodes;
	
	public List<Node> getNearbyNodes() {
		return nearbyNodes;
		
	}
	
	public boolean isFound() {
		return found;
	}
}
