package kademlia.messages;

public class StoreResponse extends Response {
	public boolean successful;
	
	public boolean isSuccessful() {
		return successful;
	}
}
