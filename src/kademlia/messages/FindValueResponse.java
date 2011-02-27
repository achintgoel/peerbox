package kademlia.messages;

public class FindValueResponse extends FindResponse {
	protected String foundValue;
	
	public String getFoundValue() {
		return foundValue;
	}
}
