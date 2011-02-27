package kademlia;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import kademlia.messages.FindRequest;
import kademlia.messages.FindResponse;


public class FindProcess<FRT extends FindResponse> {
	final int maxRequests;
	final int nearestSetSize;				
	final Set<Node> prevQueried;			// previously queried nodes
	final SortedSet<Node> nearestSet;		// nodes that have been found through all searches
	final Set<Node> current;				// nodes currently being queried but haven't replied
	FindRequest findRequest;						// target being searched for
	NetworkInstance networkInstance;
	final ResponseListener<FRT> callback;
	
	
	private FindProcess(NetworkInstance ni, FindRequest request, ResponseListener<FRT> responseListener){
		networkInstance = ni;
		maxRequests = networkInstance.getConfiguration().getAlpha() * networkInstance.getConfiguration().getAlpha();
		nearestSetSize = networkInstance.getConfiguration().getK() * 2;
		findRequest = request;
		prevQueried = new HashSet<Node>();
		nearestSet = new TreeSet<Node>(new IdentifiableDistanceComparator(findRequest.getTargetIdentifier()));
		nearestSet.addAll(networkInstance.getBuckets().getNearestNodes(findRequest.getTargetIdentifier(), networkInstance.getConfiguration().getAlpha()));
		current = new HashSet<Node>();
		callback = responseListener;
			
	}
	
	public static <T extends FindResponse> void execute(NetworkInstance ni, FindRequest request, ResponseListener<T> responseListener){
		FindProcess<T> sh = new FindProcess<T>(ni, request, responseListener);
		sh.nextIteration();
	}
	
	
	private void nextIteration(){
		// add the querySet to prevQueried and current and then search for every node
		while(current.size() < maxRequests){
			while(nearestSet.size() > nearestSetSize){
				nearestSet.remove(nearestSet.last());				
			}
			SortedSet<Node> unsearchedNodes = new TreeSet<Node>(new IdentifiableDistanceComparator(findRequest.getTargetIdentifier()));
			unsearchedNodes = nearestSet;
			unsearchedNodes.removeAll(prevQueried);
			if(unsearchedNodes.isEmpty()){
				callback.onFailure();
				// TODO: this is not right
			}
			final Node nextRequest = unsearchedNodes.first();
		  	prevQueried.add(nextRequest);
		  	current.add(nextRequest);
		  	// TODO: executeFindNode
			networkInstance.sendRequestRPC(nextRequest, findRequest, new ResponseListener<FRT>(){
		    	// event that a message was received
		    	public void responseReceived(FRT response) {
		    		// if the reply contains the target then trigger identifier found
		          	if(response.isFound()){
		          	    callback.responseReceived(response);
		          	}
		          	else{
		          		nearestSet.addAll(response.getNearbyNodes());
		    			networkInstance.getBuckets().addAll(response.getNearbyNodes());
		          		current.remove(nextRequest);
		          		nextIteration();
		          	}
		      	}		    	
		    	
		    	// event that the message timed out
		      	public void onFailure(){
		      		current.remove(nextRequest);
		      		nextIteration();
		      	}
		    });
		}
	}
}