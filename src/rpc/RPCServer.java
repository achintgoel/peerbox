package rpc;

import java.util.HashMap;
import java.util.Map;

public class RPCServer {
	final protected Map<String, ServiceListener> registeredServices;
	
	public RPCServer() {
		registeredServices = new HashMap<String, ServiceListener>();
	}
	
	public void registerServiceListener(String serviceName, ServiceListener serviceListener) {
		if (serviceListener == null) {
			registeredServices.remove(serviceName);
		} else {
			registeredServices.put(serviceName, serviceListener);
		}
	}
}
