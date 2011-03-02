package rpc;

import java.net.URI;

class RPCWaitingRequest {
	final protected RPCMessage requestMessage;
	final protected RPCResponseListener responseListener;
	final protected URI requestRecipient;
	
	public RPCWaitingRequest(RPCMessage requestMessage, URI requestRecipient, RPCResponseListener responseListener) {
		this.requestMessage = requestMessage;
		this.requestRecipient = requestRecipient;
		this.responseListener = responseListener;
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
}
