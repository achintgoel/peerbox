package kademlia;

import java.io.Serializable;
import java.util.ArrayList;

import kademlia.messages.FindNodeRequest;
import kademlia.messages.FindNodeResponse;
import kademlia.messages.FindRequest;
import kademlia.messages.FindResponse;
import kademlia.messages.FindValueRequest;
import kademlia.messages.FindValueResponse;
import kademlia.messages.PingRequest;
import kademlia.messages.PingResponse;
import kademlia.messages.Request;
import kademlia.messages.Response;
import kademlia.messages.StoreRequest;
import kademlia.messages.StoreResponse;
import rpc.RPCEvent;
import rpc.RPCResponseListener;
import rpc.RPCHandler;

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
		// TODO
	}
	
	//Maybe put in Node
	public void ping(Node target, ResponseListener<PingResponse> responseListener) {
		// TODO
	}
	
	//TODO add bootstrap function
	
	
	////////
	public void onRequestReceived(Request request){
		if(request instanceof FindRequest){
			FindResponse fr;
			ArrayList<Node> nearestNodes = (ArrayList<Node>) buckets.getNearestNodes(((FindRequest) request).getTargetIdentifier(), getConfiguration().getK());
			if(request instanceof FindValueRequest){
				fr = new FindValueResponse();
				//TODO: if value found return new FindValueResponse with the value
			}
			else{
				fr = new FindNodeResponse(); 
				if(nearestNodes.contains(((FindRequest) request).getTargetIdentifier())){ 
				}
				//TODO: if node found return new FindNodeResponse with node
			}
			// if value or node not found send findResponse with nearest k nodes 
			
		}
		else if(request instanceof StoreRequest){
			
		}
		else if(request instanceof PingRequest){
			
		}
	}
}
