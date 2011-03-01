package kademlia;

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
import rpc.ServiceRequestListener;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class KademliaRequestListener implements ServiceRequestListener {
	
	protected final Gson gson = new Gson();
	protected final NetworkInstance ni;
	
	public KademliaRequestListener(NetworkInstance networkInstance){
		this.ni = networkInstance;
	}
	
	@Override
	public void onRequestRecieved(RPCEvent e) {
		JsonParser parser = new JsonParser();
		try {
			final JsonObject root = (JsonObject) parser.parse(e.getDataString());
			String command = root.get("command").getAsString();
			Request request;
			Response response;
			if(command.equals(FindNodeRequest.command)){
				FindNodeRequest fnr = gson.fromJson(root, FindNodeRequest.class);
				Node returnNode = ni.getBuckets().findNodeByIdentifier(fnr.getTargetIdentifier());
				if(returnNode == null){
					response = new FindNodeResponse(ni.getBuckets().getNearestNodes(fnr.getTargetIdentifier(), ni.getConfiguration().getK()));
				}
				else{
					response = new FindNodeResponse(returnNode);
				}
				request = fnr;
			}
			else if(command.equals(FindValueRequest.command)){
				FindValueRequest fvr = gson.fromJson(root, FindValueRequest.class);
				// TODO: get Node closest to the value
				response = new FindValueResponse();
				request = fvr;
			}
			else if(command.equals(StoreRequest.command)){
				StoreRequest sr = gson.fromJson(root, StoreRequest.class);
				// TODO: store key value pair in the buckets
				response = new StoreResponse(false);
				request = sr;
			}
			else if(command.equals(PingRequest.command)){
				PingRequest pr = gson.fromJson(root, PingRequest.class);
				response = new PingResponse(ni.getLocalNodeIdentifier());
				request = pr;
			}
			else{
				request = null;
				response = null;
			}
			if(response != null){
				String responseString = gson.toJson(response);
				e.respond(responseString);
			}
			if(request != null){
				Node newNode = new Node(ni, e.getSenderURI(), request.getMyNodeId());
				ni.getBuckets().add(newNode);
			}
		}
		catch(Exception exception){
			
		}
	}

}
