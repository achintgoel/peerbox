package org.peerbox.kademlia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.peerbox.kademlia.messages.FindRequest;
import org.peerbox.kademlia.messages.FindResponse;


/**
 * 
 * Object created when a node or a value is searched for
 *
 * @param <FRT> the type of FindResponse FindNodeResponse or a FindValueResponse
 */
public class FindProcess<FRT extends FindResponse> {
	protected final int maxRequests;
	protected final int nearestSetSize;				
	protected final Set<Node> prevQueried;			// previously queried nodes
	protected final TreeSet<Node> nearestSet;		// nodes that have been found through all searches
	protected final Set<Node> current;				// nodes currently being queried but haven't replied
	protected final FindRequest findRequest;						// target being searched for
	protected final NetworkInstance networkInstance;
	protected final ResponseListener<FRT> callback;
	protected LinkedList<Node> unsearchedNodes;
	protected final Class<FRT> responseClass;
	protected FRT lastResponse;
	protected FRT foundResponse;
	protected final boolean stopOnFound;
	protected boolean done;
	
	
	private FindProcess(NetworkInstance ni, FindRequest request, boolean stopOnFound, Class<FRT> responseClass, ResponseListener<FRT> responseListener){
		networkInstance = ni;
		maxRequests = networkInstance.getConfiguration().getAlpha() * networkInstance.getConfiguration().getAlpha();
		nearestSetSize = networkInstance.getConfiguration().getK() * 2;
		findRequest = request;
		prevQueried = new HashSet<Node>();
		nearestSet = new TreeSet<Node>(new IdentifiableDistanceComparator(findRequest.getTargetIdentifier()));
		nearestSet.addAll(networkInstance.getBuckets().getNearestNodes(findRequest.getTargetIdentifier(), nearestSetSize));
		current = new HashSet<Node>();
		callback = responseListener;
		this.responseClass = responseClass;
		unsearchedNodes = new LinkedList<Node>(nearestSet);
		this.stopOnFound = stopOnFound;
		lastResponse = null;
		foundResponse = null;
		done = false;
	}
	
	/**
	 * 
	 * To initialize the find process
	 * @param <T> type of FindResponse
	 * @param ni NetworkInstance
	 * @param request the request to be sent
	 * @param responseListener callback
	 */
	public static <T extends FindResponse> void execute(NetworkInstance ni, FindRequest request, boolean stopOnFound, Class<T> responseClass, ResponseListener<T> responseListener){
		FindProcess<T> sh = new FindProcess<T>(ni, request, stopOnFound, responseClass, responseListener);
		sh.computeUnsearchedNodes();
		sh.nextIteration();
	}
	
	
	/**
	 * To actually run the FindProcess
	 * 
	 * 
	 */
	private void nextIteration(){
		// Until upto maxRequests are made
		while(current.size() < maxRequests && !unsearchedNodes.isEmpty()){
			// send the findRequest RPC to the first one in the unsearched nodes
			final Node nextRequestDestination = unsearchedNodes.poll();		
						// poll retrieves and removes the first element
		  	prevQueried.add(nextRequestDestination);
		  	current.add(nextRequestDestination);
			networkInstance.sendRequestRPC(nextRequestDestination, findRequest, responseClass, new ResponseListener<FRT>(){
		    	// event that a message was received
		    	public void onResponseReceived(FRT response) {
		    		// if the reply contains the target then trigger identifier found
	          		nearestSet.addAll(response.getNearbyNodes());
	    			networkInstance.getBuckets().addAll(response.getNearbyNodes());
	          		current.remove(nextRequestDestination);
		          	if(response.isFound()){
		          		if(stopOnFound){
		          			if(!done)
		          				callback.onResponseReceived(response);
		          			done = true;
		          		}
		          		else{
		          			foundResponse = response;
		        			computeUnsearchedNodes();
		          			attemptDone();
		          		}
		          	}
		          	// otherwise add the nodes to the nearestSet and to the k-buckets 
		          	else{
						lastResponse = response;
						computeUnsearchedNodes();
						attemptDone();
		          	}
		      	}		    	
		    	
		    	// event that the message timed out
		      	public void onFailure(){
		      		current.remove(nextRequestDestination);
		      		attemptDone();
		      	}
		    });
		}
	}
	
	private void attemptDone() {
		if(!done) {
			// if no new unsearched nodes and queue is over means the requested value wasn't found
			if(unsearchedNodes.isEmpty() && current.isEmpty()) {
				FRT response = foundResponse == null ? lastResponse : foundResponse;
				if(response != null) {
					// if there has been at least one response and no more unsearched nodes
					// then callback with the k nearest sets
					List<Node> nearbyNodes = new ArrayList<Node>(nearestSet);
					int k = networkInstance.getConfiguration().getK();
					List<Node> kNearbyNodes = nearbyNodes.subList(0, nearestSet.size() > k ? k : nearestSet.size());
					response.setNearbyNodes(kNearbyNodes);
					callback.onResponseReceived(response);	
				}
				else {
		  			callback.onFailure();
		  		}
			}
			else if(!unsearchedNodes.isEmpty()){
				nextIteration();
			}
		}
	}
	
	private void computeUnsearchedNodes(){			
		// resize the nearestSet by removing the last elements
/*		while(nearestSet.size() > nearestSetSize){
			nearestSet.remove(nearestSet.last());				
		}*/
		// find the unsearched nodes by removing the previously queried from the nearest set
		LinkedList<Node> temp = new LinkedList<Node>();
		int iteration = 0;
		for(Node node : nearestSet){
			if(iteration == nearestSetSize)
				break;
			if(!prevQueried.contains(node)){
				temp.add(node);				
			}
			iteration++;
		}
		unsearchedNodes = temp;		
	}
}