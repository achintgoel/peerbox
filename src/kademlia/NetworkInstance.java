package kademlia;

import java.io.Serializable;

import kademlia.messages.Request;
import dht.DistributedMap;

public class NetworkInstance {
	protected Identifier localIdentifier;
	protected Buckets buckets;
	
	public NetworkInstance() {
		
	}
	
	public Configuration getConfiguration() {
		return new Configuration();
	}
	
	public Identifier getLocalNodeIdentifier() {
		return localIdentifier;
	}
	
	public DistributedMap<Serializable, Serializable> getPrimaryDHT() {
		return null;
	}
	
	protected void sendRequestRPC(Node destination, Request requestRPC, ResponseListener callback) {
		
	}

	public Buckets getBuckets() {
		// TODO Auto-generated method stub
		return null;
	}
}
