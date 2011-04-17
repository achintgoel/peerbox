package org.peerbox.rpc.json;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.peerbox.network.IncomingMessage;
import org.peerbox.network.MessageListener;
import org.peerbox.network.MessageServer;
import org.peerbox.rpc.RPCEvent;
import org.peerbox.rpc.RPCHandler;
import org.peerbox.rpc.RPCMessage;
import org.peerbox.rpc.RPCResponseListener;
import org.peerbox.rpc.RPCServiceRequestListener;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class JsonRPCHandler implements RPCHandler {
	final protected Map<String, RPCServiceRequestListener> registeredServices;
	final static protected String VERSION = "1.0";
	final protected int TIMEOUT_SECS = 10;
	final protected Timer timeoutTimer;
	final protected Gson gson = new Gson();
	final protected Map<String, WaitingRequest> waitingRequests;
	protected URI myURI;
	final protected MessageServer messageServer;
	
	public JsonRPCHandler(MessageServer messageServer) {
		this.messageServer = messageServer;
		messageServer.setListener(newListener());
		messageServer.start();
		registeredServices = new HashMap<String, RPCServiceRequestListener>();
		waitingRequests = Collections.synchronizedMap(new HashMap<String, WaitingRequest>());
		timeoutTimer = new Timer(true);
	}

	@Override
	public URI getLocalURI() {
		if (myURI == null) {
			return messageServer.getSender().getLocalURI();
		} else {
			return myURI;
		}
	}

	@Override
	public void setLocalURI(URI uri) {
		this.myURI = uri;
	}

	@Override
	public void registerServiceListener(String serviceName, RPCServiceRequestListener serviceListener) {
		if (serviceListener == null) {
			registeredServices.remove(serviceName);
		} else {
			registeredServices.put(serviceName, serviceListener);
		}
	}

	@Override
	public void sendRequest(URI recipient, String serviceName, String dataString,
			final RPCResponseListener responseListener) {
		final String uuid = UUID.randomUUID().toString(); // Is this safe to
															// assume absolute
															// local uniqueness?
		RPCMessage requestMessage = new RPCMessage(VERSION, serviceName, uuid, dataString, null);
		TimerTask timeoutTask = new TimerTask() {
			@Override
			public void run() {
				if (waitingRequests.remove(uuid) != null) {
					responseListener.onTimeout();
				}
			}
		};
		waitingRequests.put(uuid, new WaitingRequest(requestMessage, recipient, responseListener, timeoutTask));
		messageServer.getSender().sendData(recipient, gson.toJson(requestMessage));
		try {
			timeoutTimer.schedule(timeoutTask, 1000 * TIMEOUT_SECS);
		} catch (IllegalStateException e) {
			// This is OK; timer was already canceled because the response beat
			// setting
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
					RPCServiceRequestListener service = registeredServices.get(rpcMessage.getService());
					if (service != null) {
						service.onRequestRecieved(new RPCEvent() {

							@Override
							public void respond(String data) {
								RPCMessage responseMessage = new RPCMessage(VERSION, rpcMessage.getService(),
										rpcMessage.getId(), null, data);
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
					WaitingRequest waitingRequest = waitingRequests.get(rpcMessage.getId());
					// &&
					// waitingRequest.getRequestRecipient().equals(message.getSenderURI())
					if (waitingRequest != null
							&& waitingRequest.getRequestMessage().getService().equals(rpcMessage.getService())) {
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
					} else {
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
