package kademlia;

import kademlia.messages.Response;

public interface ResponseListener<RT extends Response> {
	void responseReceived(RT response);
	void onFailure();
}
