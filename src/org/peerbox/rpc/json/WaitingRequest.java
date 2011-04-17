package org.peerbox.rpc.json;

import java.net.URI;
import java.util.TimerTask;

import org.peerbox.rpc.RPCMessage;
import org.peerbox.rpc.RPCResponseListener;

class WaitingRequest {
	final protected RPCMessage requestMessage;
	final protected RPCResponseListener responseListener;
	final protected URI requestRecipient;
	final protected TimerTask timeoutTask;

	public WaitingRequest(RPCMessage requestMessage, URI requestRecipient, RPCResponseListener responseListener,
			TimerTask timeoutTask) {
		this.requestMessage = requestMessage;
		this.requestRecipient = requestRecipient;
		this.responseListener = responseListener;
		this.timeoutTask = timeoutTask;
	}

	/**
	 * 
	 * @return The RPCMessage sent as a Request
	 */
	public RPCMessage getRequestMessage() {
		return requestMessage;
	}

	/**
	 * 
	 * @return The RPCResponseListener waiting for a response to this request
	 */
	public RPCResponseListener getResponseListener() {
		return responseListener;
	}

	/*
	 * @return The URI of the recipient of this Request
	 */
	public URI getRequestRecipient() {
		return requestRecipient;
	}

	public TimerTask getTimeoutTask() {
		return timeoutTask;
	}
}
