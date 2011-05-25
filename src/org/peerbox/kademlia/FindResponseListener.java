package org.peerbox.kademlia;

import org.peerbox.kademlia.messages.FindResponse;
import org.peerbox.kademlia.messages.FindValueResponse;

public abstract class FindResponseListener<RT extends FindResponse> implements ResponseListener<RT> {
	public void onFindComplete(Node nearestNotFound, FindValueResponse response){
		
	}
}
