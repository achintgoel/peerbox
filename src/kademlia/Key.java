package kademlia;

import dht.CompositeKey;

public class Key extends CompositeKey<String, String> implements Identifiable {
	
	public Key(String primaryKey, String secondaryKey) {
		super(primaryKey, secondaryKey);
	}

	@Override
	public Identifier getIdentifier() {
		// TODO: Identifier generation
		return null;
	}

}
