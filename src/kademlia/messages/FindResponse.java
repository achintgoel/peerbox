package kademlia.messages;

public abstract class FindResponse extends Response {
	protected boolean found;
	
	public boolean isFound() {
		return found;
	}
}
