package kademlia;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import kademlia.messages.FindNodeRequest;
import kademlia.messages.FindNodeResponse;
import kademlia.messages.FindValueRequest;
import kademlia.messages.FindValueResponse;
import kademlia.messages.PingRequest;
import kademlia.messages.PingResponse;
import kademlia.messages.Request;
import kademlia.messages.Response;
import kademlia.messages.StoreRequest;
import kademlia.messages.StoreResponse;
import rpc.RPCEvent;
import rpc.RPCHandler;
import rpc.RPCResponseListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import dht.DistributedMap;

public class NetworkInstance {
	protected Identifier localIdentifier;
	protected Buckets buckets;
	protected final Gson gson;
	protected final String rpcServiceName;
	
	public NetworkInstance() {
		gson = new Gson();
		rpcServiceName = "kad";
	}
	
	public Configuration getConfiguration() {
		return new Configuration();
	}
	
	public Identifier getLocalNodeIdentifier() {
		return localIdentifier;
	}
	
	public DistributedMap<Serializable, Serializable> getPrimaryDHT() {
		return null;
	}
	
	protected <T extends Response> void sendRequestRPC(Node destination, Request request, final ResponseListener<T> callback) {
		String requestData = gson.toJson(request);
		getRPC().sendRequest(destination.getNetworkURI(), rpcServiceName, requestData, new RPCResponseListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onResponseReceived(RPCEvent event) {
				try {
					callback.onResponseReceived((T) gson.fromJson(event.getDataString(), new TypeToken<T>(){}.getType()));
				} catch (Exception e) {
					//
				}
			}
		});
	}
	
	public RPCHandler getRPC() {
		return null; //TODO
	}

	public Buckets getBuckets() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void findNode(Identifier targetNodeId, ResponseListener<FindNodeResponse> responseListener) {
		FindNodeRequest request = new FindNodeRequest(getLocalNodeIdentifier(), targetNodeId);
		FindProcess.execute(this, request, responseListener);
	}
	
	public void findValue(Key targetKey, ResponseListener<FindValueResponse> responseListener) {
		FindValueRequest request = new FindValueRequest(getLocalNodeIdentifier(), targetKey);
		FindProcess.execute(this, request, responseListener);
	}
	
	public void storeValue(Key key, String value, ResponseListener<StoreResponse> responseListener) {
		StoreRequest request = new StoreRequest(getLocalNodeIdentifier(), key, value);
		StoreProcess.execute(this, request, responseListener);
	}
	
	//Maybe put in Node
	public void ping(Node targetNode, ResponseListener<PingResponse> responseListener) {
		PingRequest request = new PingRequest(getLocalNodeIdentifier(), targetNode.getIdentifier());
		this.sendRequestRPC(targetNode, request, responseListener);
	}
	
	//TODO add bootstrap function
	public void bootstrap(List<URI> friends, BootstrapListener bootstrapListener) {
		BootstrapProcess.execute(this, friends, bootstrapListener);
	}
}
