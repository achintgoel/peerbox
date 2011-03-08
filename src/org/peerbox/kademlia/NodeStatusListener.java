package org.peerbox.kademlia;

public interface NodeStatusListener {
	public void onNodeAlive(Node node);
	public void onNodeDown(Node node);
}
