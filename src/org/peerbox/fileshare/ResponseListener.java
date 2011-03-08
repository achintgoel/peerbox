package org.peerbox.fileshare;

import org.peerbox.fileshare.messages.Response;


public interface ResponseListener<RT extends Response> {
	void onResponseReceived(RT response);
	void onFailure();
}
