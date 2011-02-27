package rpc;

import java.util.HashMap;
import java.util.Map;

import network.IncomingMessage;
import network.MessageListener;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class RPCServer {
	final protected Map<String, ServiceListener> registeredServices;
	
	protected RPCServer() {
		registeredServices = new HashMap<String, ServiceListener>();
	}
	
	public void registerServiceListener(String serviceName, ServiceListener serviceListener) {
		if (serviceListener == null) {
			registeredServices.remove(serviceName);
		} else {
			registeredServices.put(serviceName, serviceListener);
		}
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
					
				}
			} catch (JsonParseException e) {
				
			} catch (ClassCastException e) {
				
			}
			
		}
	}
}
