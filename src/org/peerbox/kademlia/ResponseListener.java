package org.peerbox.kademlia;

import org.peerbox.kademlia.messages.Response;

public interface ResponseListener<RT extends Response> {
	void onResponseReceived(RT response);

	void onFailure();
}
