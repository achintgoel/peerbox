package org.peerbox.kademlia.messages;

import java.util.List;

import org.peerbox.kademlia.Node;

public abstract class FindResponse extends Response {
	protected boolean found;
	protected List<Node> nearbyNodes;

	public List<Node> getNearbyNodes() {
		return nearbyNodes;
	}

	public void setNearbyNodes(List<Node> nearestNodes) {
		nearbyNodes = nearestNodes;
	}

	public boolean isFound() {
		return found;
	}
}
