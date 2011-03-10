package org.peerbox.rpc;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.peerbox.network.IncomingMessage;
import org.peerbox.network.MessageListener;
import org.peerbox.network.MessageSender;
import org.peerbox.network.MessageServerHandler;


import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class RPCHandler {
	final protected Map<String, ServiceRequestListener> registeredServices;
	protected MessageSender messageSender;
	final static protected String VERSION = "1.0";
	final protected int TIMEOUT_SECS = 10;
	final protected Timer timeoutTimer;
	final protected Gson gson = new Gson();
	final protected Map<String, RPCWaitingRequest> waitingRequests;
	protected URI myURI;
	
	protected RPCHandler() {
		registeredServices = new HashMap<String, ServiceRequestListener>();
		waitingRequests = Collections.synchronizedMap(new HashMap<String, RPCWaitingRequest>());
		timeoutTimer = new Timer(true);
	}
	
	public static RPCHandler getUDPInstance(int port) {
		RPCHandler rpcHandler = new RPCHandler();
		rpcHandler.messageSender = MessageServerHandler.startUDPServer(port, rpcHandler.newListener());
		return rpcHandler;
	}
	
	public URI getLocalURI() {
		if (myURI == null) {
			return messageSender.getLocalURI();
		} else {
			return myURI;
		}
	}
	
	public void setLocalURI(URI uri) {
		this.myURI = uri;
	}
	
	public void registerServiceListener(String serviceName, ServiceRequestListener serviceListener) {
		if (serviceListener == null) {
			registeredServices.remove(serviceName);
		} else {
			registeredServices.put(serviceName, serviceListener);
		}
	}
	
	public void sendRequest(URI recipient, String serviceName, String dataString, final RPCResponseListener responseListener) {
		final String uuid = UUID.randomUUID().toString(); // Is this safe to assume absolute local uniqueness?
		RPCMessage requestMessage = new RPCMessage(VERSION, serviceName, uuid, dataString, null);
		TimerTask timeoutTask = new TimerTask() {
			@Override
			public void run() {
				if (waitingRequests.remove(uuid) != null) {
					responseListener.onTimeout();
				}
			}
		};
		waitingRequests.put(uuid, new RPCWaitingRequest(requestMessage, recipient, responseListener, timeoutTask));
		messageSender.sendData(recipient, gson.toJson(requestMessage));
		try {
			timeoutTimer.schedule(timeoutTask, 1000 * TIMEOUT_SECS);
		} catch (IllegalStateException e) {
			//This is OK; timer was already canceled because the response beat setting 
		}
	}
	
	IncomingMessageListener newListener() {
		return new IncomingMessageListener();
	}
	
	class IncomingMessageListener implements MessageListener {
		@Override
		public void onMessage(final IncomingMessage message) {
			try {
				final RPCMessage rpcMessage = gson.fromJson(message.getDataString(), RPCMessage.class);
				if (!rpcMessage.getVersion().equals(VERSION)) {
					// Unsupported Message Version
					// System.out.println("Unsupported Message Version");   
				}
				if (rpcMessage.getRequest() != null) {
					ServiceRequestListener service = registeredServices.get(rpcMessage.getService());
					if (service != null) {
						service.onRequestRecieved(new RPCEvent() {

							@Override
							public void respond(String data) {
								RPCMessage responseMessage = new RPCMessage(VERSION, rpcMessage.getService(), rpcMessage.getId(), null, data);
								message.sendResponse(gson.toJson(responseMessage));
							}

							@Override
							public String getDataString() {
								return rpcMessage.getRequest();
							}

							@Override
							public String getServiceName() {
								return rpcMessage.getService();
							}

							@Override
							public URI getSenderURI() {
								return message.getSenderURI();
							}
							
						});
					}
				} else if (rpcMessage.getResponse() != null) {
					RPCWaitingRequest waitingRequest = waitingRequests.get(rpcMessage.getId());
					// && waitingRequest.getRequestRecipient().equals(message.getSenderURI())
					if (waitingRequest != null && waitingRequest.getRequestMessage().getService().equals(rpcMessage.getService())) {
						waitingRequest.timeoutTask.cancel();
						waitingRequests.remove(rpcMessage.getId());
						waitingRequest.getResponseListener().onResponseReceived(new RPCEvent() {
							@Override
							public void respond(String data) {
								throw new UnsupportedOperationException("Cannot respond to a request response");
							}

							@Override
							public String getDataString() {
								return rpcMessage.getResponse();
							}

							@Override
							public String getServiceName() {
								return rpcMessage.getService();
							}

							@Override
							public URI getSenderURI() {
								return message.getSenderURI();
							}
						});
					}
					else{
						// System.out.println("No matching request found for the received response");
					}
				}
			} catch (JsonParseException e) {
				e.printStackTrace();
				
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
			
		}
	}
}
