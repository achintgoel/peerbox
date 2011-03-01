package rpc;

import java.io.Console;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import network.IncomingMessage;
import network.MessageListener;
import network.MessageSender;
import network.MessageServerHandler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class RPCHandler {
	final protected Map<String, ServiceRequestListener> registeredServices;
	protected MessageSender messageSender;
	final static protected String VERSION = "1.0";
	final protected Gson gson = new Gson();
	
	protected RPCHandler() {
		registeredServices = new HashMap<String, ServiceRequestListener>();
	}
	
	public static RPCHandler getUDPInstance(int port) {
		RPCHandler rpcHandler = new RPCHandler();
		rpcHandler.messageSender = MessageServerHandler.startUDPServer(port, rpcHandler.newListener());
		return rpcHandler;
	}
	
	public void registerServiceListener(String serviceName, ServiceRequestListener serviceListener) {
		if (serviceListener == null) {
			registeredServices.remove(serviceName);
		} else {
			registeredServices.put(serviceName, serviceListener);
		}
	}
	
	public void sendRequest(URI recipient, String serviceName, String dataString, RPCResponseListener responseListener) {
		//TODO
	}
	
	IncomingMessageListener newListener() {
		return new IncomingMessageListener();
	}
	
	class IncomingMessageListener implements MessageListener {
		@Override
		public void onMessage(final IncomingMessage message) {
			JsonParser parser = new JsonParser();
			try {
				final JsonObject root = (JsonObject) parser.parse(message.getDataString());
				final RPCMessage rpcMessage = gson.fromJson(root, RPCMessage.class);
				if (rpcMessage.getVersion() != VERSION) {
					// Unsupported Message Version
					System.out.println("Unsupported Message Version");   
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
								return root.get("request").getAsString();
							}

							@Override
							public String getServiceName() {
								return rpcMessage.getService();
							}
							
						});
					}
				} else if (rpcMessage.getResponse() != null) {
					
				}
			} catch (JsonParseException e) {
				
			} catch (ClassCastException e) {
				
			}
			
		}
	}
}
