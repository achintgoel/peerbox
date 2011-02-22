package kademlia;

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
}
