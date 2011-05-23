package org.peerbox.kademlia.messages;

public class StoreResponse extends Response {
	public boolean successful;

	protected StoreResponse() {

	}

	public StoreResponse(boolean successful) {
		this.successful = successful;
	}

	public boolean isSuccessful() {
		return successful;
	}
}
