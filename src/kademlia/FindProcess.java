package kademlia;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import kademlia.messages.FindRequest;
import kademlia.messages.FindResponse;


public class FindProcess<FRT extends FindResponse> {
	protected final int maxRequests;
	protected final int nearestSetSize;				
	protected final Set<Node> prevQueried;			// previously queried nodes
	protected final SortedSet<Node> nearestSet;		// nodes that have been found through all searches
	protected final Set<Node> current;				// nodes currently being queried but haven't replied
	protected final FindRequest findRequest;						// target being searched for
	protected final NetworkInstance networkInstance;
	protected final ResponseListener<FRT> callback;
	
	
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
			final Node nextRequestDestination = unsearchedNodes.first();
		  	prevQueried.add(nextRequestDestination);
		  	current.add(nextRequestDestination);
		  	// TODO: executeFindNode
			networkInstance.sendRequestRPC(nextRequestDestination, findRequest, new ResponseListener<FRT>(){
		    	// event that a message was received
		    	public void onResponseReceived(FRT response) {
		    		// if the reply contains the target then trigger identifier found
		          	if(response.isFound()){
		          	    callback.onResponseReceived(response);
		          	}
		          	else{
		          		nearestSet.addAll(response.getNearbyNodes());
		    			networkInstance.getBuckets().addAll(response.getNearbyNodes());
		          		current.remove(nextRequestDestination);
		          		nextIteration();
		          	}
		      	}		    	
		    	
		    	// event that the message timed out
		      	public void onFailure(){
		      		current.remove(nextRequestDestination);
		      		nextIteration();
		      	}
		    });
		}
	}
}