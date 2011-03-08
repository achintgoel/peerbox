package org.peerbox.kademlia;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.peerbox.dht.CompositeKey;


public class Key extends CompositeKey<String, String> implements Identifiable {
	
	protected Key(){
		super();
	}
	
	public Key(String primaryKey, String secondaryKey) {
		super(primaryKey, secondaryKey);
	}

	@Override
	public Identifier getIdentifier() {
		
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("SHA");
			md.reset();
			md.update(primaryKey.getBytes());
			md.update("::".getBytes());
			md.update(secondaryKey.getBytes());
			byte[] digest = md.digest();
			
			return Identifier.fromBytes(digest);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException("Java Runtime does not support SHA-1");
		}
		
		
	}

}
