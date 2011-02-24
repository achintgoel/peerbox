package kademlia;

import java.io.Serializable;
import java.util.ArrayList;

import kademlia.messages.FindNodeResponse;
import kademlia.messages.FindRequest;
import kademlia.messages.FindResponse;
import kademlia.messages.FindValueRequest;
import kademlia.messages.FindValueResponse;
import kademlia.messages.PingRequest;
import kademlia.messages.Request;
import kademlia.messages.StoreRequest;
import dht.DistributedMap;

public class NetworkInstance {
	protected Identifier localIdentifier;
	protected Buckets buckets;
	
	public NetworkInstance() {
		
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
	
	protected void sendRequestRPC(Node destination, Request requestRPC, ResponseListener callback) {
		
	}

	public Buckets getBuckets() {
		// TODO Auto-generated method stub
		return null;
	}
	
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
