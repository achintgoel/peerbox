package org.peerbox.kademlia;

import java.util.List;

import org.peerbox.kademlia.messages.FindNodeRequest;
import org.peerbox.kademlia.messages.FindNodeResponse;
import org.peerbox.kademlia.messages.FindValueRequest;
import org.peerbox.kademlia.messages.FindValueResponse;
import org.peerbox.kademlia.messages.PingRequest;
import org.peerbox.kademlia.messages.PingResponse;
import org.peerbox.kademlia.messages.Request;
import org.peerbox.kademlia.messages.Response;
import org.peerbox.kademlia.messages.StoreRequest;
import org.peerbox.kademlia.messages.StoreResponse;
import org.peerbox.rpc.RPCEvent;
import org.peerbox.rpc.RPCServiceRequestListener;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * The listener to handle the received kademlia requests
 *
 */
public class KademliaRequestListener implements RPCServiceRequestListener {
	
	protected final Gson gson = new Gson();
	protected final NetworkInstance ni;
	
	public KademliaRequestListener(NetworkInstance networkInstance) {
		this.ni = networkInstance;
	}
	
	/**
	 * the function called on receiving a request
	 */
	@Override
	public void onRequestRecieved(RPCEvent e) {
		JsonParser parser = new JsonParser();
		try {
			// Get the command type
			final JsonObject root = (JsonObject) parser.parse(e.getDataString());
			String command = root.get("command").getAsString();
			Request request;
			Response response;
			if(command.equals(FindNodeRequest.COMMAND)){
				FindNodeRequest fnr = gson.fromJson(root, FindNodeRequest.class);
				Node returnNode = ni.getBuckets().findNodeByIdentifier(fnr.getTargetIdentifier());
				if(returnNode == null) {
					response = new FindNodeResponse(ni.getBuckets().getNearestNodes(fnr.getTargetIdentifier(), ni.getConfiguration().getK()));
				}
				else{
					response = new FindNodeResponse(returnNode, ni.getBuckets().getNearestNodes(fnr.getTargetIdentifier(), ni.getConfiguration().getK()));
				}
				request = fnr;
			}
			else if(command.equals(FindValueRequest.COMMAND)){
				FindValueRequest fvr = gson.fromJson(root, FindValueRequest.class);
				List<Value> returnValue = ni.getLocalDataStore().get(fvr.getKey());
				if(returnValue == null) {
					response = new FindValueResponse(ni.getBuckets().getNearestNodes(fvr.getTargetIdentifier(), ni.getConfiguration().getK()));
				}
				else{
					response = new FindValueResponse(returnValue, ni.getBuckets().getNearestNodes(fvr.getTargetIdentifier(), ni.getConfiguration().getK()));
				}
				request = fvr;
			}
			else if(command.equals(StoreRequest.COMMAND)){
				StoreRequest sr = gson.fromJson(root, StoreRequest.class);
				boolean success = ni.storeValueLocal(sr.getKey(), sr.getValue(), false);
				response = new StoreResponse(success);
				request = sr;
			}
			else if(command.equals(PingRequest.COMMAND)){
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
				Node newNode = new Node(e.getSenderURI(), request.getMyNodeId());
				ni.getBuckets().add(newNode);
			}
		}
		catch(Exception exception){
			exception.printStackTrace();
		}
	}

}
