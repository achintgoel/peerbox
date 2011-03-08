package org.peerbox.friendpeer;

import java.net.URI;
import java.security.PublicKey;

public class Friend {
	protected String alias;
	protected URI address;
	protected PublicKey pubKey;
	protected FriendManager manager;
	
	Friend(FriendManager fm, String al, URI address, PublicKey pub) {
		manager = fm;
		alias = al;
		this.address = address;
		pubKey = pub;
	}
	protected Friend() {
		
	}
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		String oldAlias = this.alias;
		this.alias = alias;
		manager.updateFriend(oldAlias, this.alias, this);
	}

	public URI getNetworkAddress() {
		return address;
	}

	public void setAddress(URI address) {
		this.address = address;
	}

	public PublicKey getPubKey() {
		return pubKey;
	}


	public FriendManager getManager() {
		return manager;
	}

	
}
