package rpc;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import network.IncomingMessage;
import network.MessageListener;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class RPCServer {
	final protected Map<String, ServiceRequestListener> registeredServices;
	
	protected RPCServer() {
		registeredServices = new HashMap<String, ServiceRequestListener>();
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
		public void onMessage(IncomingMessage message) {
			JsonParser parser = new JsonParser();
			try {
				JsonObject root = (JsonObject) parser.parse(message.getDataString());
				String version = root.get("pbox").getAsString();
				if (version.equals("1.0")) {
					String service = root.get("service").getAsString();
					if (registeredServices.containsKey(service) || registeredServices.containsKey("*")) {
						
					}
				}
			} catch (JsonParseException e) {
				
			} catch (ClassCastException e) {
				
			}
			
		}
	}
}
