package kademlia;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.Date;

public class Node implements Identifiable{
	protected Identifier nodeID;
	protected InetSocketAddress address;
	protected Date lastSeen;
	protected NetworkInstance networkInstance;
	
	public Node(NetworkInstance networkInstance, InetSocketAddress address, Identifier nodeID) {
		this.networkInstance = networkInstance;
		this.address = address;
		this.nodeID = nodeID;
	}
	
	public InetSocketAddress getAddress() {
		return address;
	}
	
	public BigInteger getDistance(Node other) {
		return Identifier.calculateDistance(this.nodeID, other.nodeID);
	}
	
	public Identifier getIdentifier() {
		return nodeID;
	}
	
	public void onAlive() {
		lastSeen = new Date();
	}
	
	public void ping(PingReplyReceiver pingReceiver) {
		
	}
}
