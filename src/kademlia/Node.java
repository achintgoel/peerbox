package kademlia;

import java.math.BigInteger;
import java.net.URI;
import java.util.Date;

public class Node implements Identifiable{
	protected Identifier nodeID;
	protected URI uri;
	protected Date lastSeen;
	protected NetworkInstance networkInstance;
	
	public Node(NetworkInstance networkInstance, URI uri, Identifier nodeID) {
		this.networkInstance = networkInstance;
		this.uri = uri;
		this.nodeID = nodeID;
	}
	
	public Node(NetworkInstance networkInstance, URI uri){
		this.networkInstance = networkInstance;
		this.uri = uri;
		this.nodeID = null;
	}
	
	public URI getNetworkURI() {
		return uri;
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
	
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		
		if (!(o instanceof Node)) {
			return false;
		}
		
		Node n = (Node) o;
		
		return nodeID.equals(n.nodeID);
	}
	
	@Override
	public int hashCode() {
		return nodeID.hashCode();
	}
}
