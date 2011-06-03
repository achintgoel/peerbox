package org.peerbox.rpc;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock RPCRouter
 * @author rajiv
 * 
 * TODO: actual timeout support
 */
public class MockRPCRouter {
	
	private HashMap<URI, MockRPCHandler> rpcMap;
	private int uriID;
	
	public MockRPCRouter() {
		uriID = 0;
		rpcMap = new HashMap<URI, MockRPCHandler>();
	}
	
	public synchronized RPCHandler getNewRPCHandler() {
		URI mockURI;
		try {
			mockURI = new URI("mock://node" + uriID++);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		MockRPCHandler mockRPC = new MockRPCHandler(mockURI, this);
		rpcMap.put(mockURI, mockRPC);
		return mockRPC;
	}
	
	public void removeRPCHandler(RPCHandler rpcHandler) {
		rpcMap.remove(rpcHandler.getLocalURI());
	}
	
	public void sendRPCEvent(URI destination, RPCEvent e, RPCResponseListener rpcResponseListener) {
		MockRPCHandler handler = rpcMap.get(destination);
		if (handler == null) {
			rpcResponseListener.onTimeout();
		} else {
			handler.onRequestReceived(e);
		}
	}
	
	class MockRPCHandler implements RPCHandler {
		
		final protected Map<String, RPCServiceRequestListener> registeredServices;
		final protected URI localURI;
		final protected MockRPCRouter router;
		
		public MockRPCHandler(URI localURI, MockRPCRouter router) {
			this.localURI = localURI;
			this.registeredServices = new HashMap<String, RPCServiceRequestListener>();
			this.router = router;
		}
		
		@Override
		public URI getLocalURI() {
			return localURI;
		}

		@Override
		public void setLocalURI(URI uri) {
			throw new UnsupportedOperationException();
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
		public void sendRequest(final URI recipient, final String serviceName, final String requestData, final RPCResponseListener responseListener) {
			router.sendRPCEvent(recipient, new RPCEvent() {

							@Override
							public void respond(final String responseData) {
								responseListener.onResponseReceived(new RPCEvent() {

									@Override
									public void respond(String data) {
										throw new UnsupportedOperationException("Cannot respond to a request response");
									}

									@Override
									public String getDataString() {
										return responseData;
									}

									@Override
									public String getServiceName() {
										return serviceName;
									}

									@Override
									public URI getSenderURI() {
										// TODO Auto-generated method stub
										return recipient;
									}
									
								});
							}

							@Override
							public String getDataString() {
								return requestData;
							}

							@Override
							public String getServiceName() {
								return serviceName;
							}

							@Override
							public URI getSenderURI() {
								return localURI;
							}

						}, responseListener);
		}
		
		void onRequestReceived(RPCEvent e) {
			RPCServiceRequestListener service = registeredServices.get(e.getServiceName());
			if (service != null) {
				service.onRequestRecieved(e);
			} else {
				//service not found
			}
		}
	}
}
