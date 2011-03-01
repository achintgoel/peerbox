package kademlia.messages;

public class StoreResponse extends Response {
	public boolean successful;
	
	public StoreResponse(boolean successful){
		this.successful = successful;
	}
	
	public boolean isSuccessful() {
		return successful;
	}
}
