package kademlia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import kademlia.messages.FindRequest;
import kademlia.messages.FindResponse;

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
	protected final TreeSet<Node> unsearchedNodes;
	protected final Class<FRT> responseClass;
	protected FRT lastResponse;
	
	
	private FindProcess(NetworkInstance ni, FindRequest request, Class<FRT> responseClass, ResponseListener<FRT> responseListener){
		networkInstance = ni;
		maxRequests = networkInstance.getConfiguration().getAlpha() * networkInstance.getConfiguration().getAlpha();
		nearestSetSize = networkInstance.getConfiguration().getK() * 2;
		findRequest = request;
		prevQueried = new HashSet<Node>();
		nearestSet = new TreeSet<Node>(new IdentifiableDistanceComparator(findRequest.getTargetIdentifier()));
		nearestSet.addAll(networkInstance.getBuckets().getNearestNodes(findRequest.getTargetIdentifier(), networkInstance.getConfiguration().getAlpha()));
		current = new HashSet<Node>();
		callback = responseListener;
		this.responseClass = responseClass;
		unsearchedNodes = (TreeSet) nearestSet.clone();
		lastResponse = null;
	}
	
	/**
	 * 
	 * To initialize the find process
	 * @param <T> type of FindResponse
	 * @param ni NetworkInstance
	 * @param request the request to be sent
	 * @param responseListener callback
	 */
	public static <T extends FindResponse> void execute(NetworkInstance ni, FindRequest request, Class<T> responseClass, ResponseListener<T> responseListener){
		FindProcess<T> sh = new FindProcess<T>(ni, request, responseClass, responseListener);
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
		while(current.size() < maxRequests){
			// send the findRequest RPC to the first one in the unsearched nodes
			final Node nextRequestDestination = unsearchedNodes.first();
		  	prevQueried.add(nextRequestDestination);
		  	current.add(nextRequestDestination);
			networkInstance.sendRequestRPC(nextRequestDestination, findRequest, responseClass, new ResponseListener<FRT>(){
		    	// event that a message was received
		    	public void onResponseReceived(FRT response) {
		    		// if the reply contains the target then trigger identifier found
		          	if(response.isFound()){
		          	    callback.onResponseReceived(response);
		          	}
		          	// otherwise add the nodes to the nearestSet and to the k-buckets 
		          	else{
		          		nearestSet.addAll(response.getNearbyNodes());
		    			networkInstance.getBuckets().addAll(response.getNearbyNodes());
		          		current.remove(nextRequestDestination);
		          		computeUnsearchedNodes();
		    			// if no new unsearched nodes that means the requested value wasn't found
		    			if(unsearchedNodes.isEmpty()){
		    				ArrayList<Node> nearbyNodes = new ArrayList<Node>(nearestSet);
		    				int k = networkInstance.getConfiguration().getK();
		    				nearbyNodes = (ArrayList<Node>) nearbyNodes.subList(0, nearestSet.size() > k ? k : nearestSet.size());
		    				response.setNearbyNodes(nearbyNodes);
		    				callback.onResponseReceived(response);
		    			}
	    				lastResponse = response;
		          		nextIteration();
		          	}
		      	}		    	
		    	
		    	// event that the message timed out
		      	public void onFailure(){
		      		current.remove(nextRequestDestination);
		      		if(current.isEmpty()){
		      			// if there has been at least one response and no more unsearched nodes
		      			// then callback with the k nearest sets
		      			if(lastResponse != null){
		      				computeUnsearchedNodes();
		      				if(unsearchedNodes.isEmpty()){
			    				ArrayList<Node> nearbyNodes = new ArrayList<Node>(nearestSet);
			    				int k = networkInstance.getConfiguration().getK();
			    				nearbyNodes = (ArrayList<Node>) nearbyNodes.subList(0, nearestSet.size() > k ? k : nearestSet.size());
		      					lastResponse.setNearbyNodes(nearbyNodes);
			      				callback.onResponseReceived(lastResponse);	
		      				}
		      			}
		      			else{
		      				callback.onFailure();
		      			}
		      		}
		      		nextIteration();
		      	}
		    });
		}
	}
	
	private void computeUnsearchedNodes(){			
		// resize the nearestSet by removing the last elements
		while(nearestSet.size() > nearestSetSize){
			nearestSet.remove(nearestSet.last());				
		}
		// find the unsearched nodes by removing the previously queried from the nearest set
		SortedSet<Node> unsearchedNodes = (TreeSet<Node>) nearestSet.clone();
		unsearchedNodes.removeAll(prevQueried);		
	}
}