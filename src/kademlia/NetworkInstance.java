package kademlia;

import java.io.Serializable;

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
	
	public Buckets getBuckets(){
		return buckets;
	}
}
