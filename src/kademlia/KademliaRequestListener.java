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


/**
 * The listener to handle the received kademlia requests
 *
 */
public class KademliaRequestListener implements ServiceRequestListener {
	
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
				String returnValue = ni.getLocalDataStore().get(fvr.getKey());
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
				boolean success = ni.getLocalDataStore().put(sr.getKey(), sr.getValue());
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
