package kademlia;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;
import java.util.HashSet;
import java.util.TreeSet;

import javax.swing.Timer;

import kademlia.messages.FindRequest;
import kademlia.messages.FindResponse;


public class SearchHandler {
	final int maxRequests;
	final int nearestSetSize;				
	final Set<Node> prevQueried;			// previously queried nodes
	final SortedSet<Node> nearestSet;		// nodes that have been found through all searches
	final Set<Node> current;				// nodes currently being queried but haven't replied
	FindRequest target;						// target being searched for
	NetworkInstance networkInstance;

	
	
	private SearchHandler(NetworkInstance ni, FindRequest request){
		networkInstance = ni;
		maxRequests = networkInstance.getConfiguration().getAlpha() * networkInstance.getConfiguration().getAlpha();
		nearestSetSize = networkInstance.getConfiguration().getK() * 2;
		target = request;
		prevQueried = new HashSet<Node>();
		nearestSet = new TreeSet<Node>(new IdentifiableDistanceComparator(target.getTargetIdentifier()));
		nearestSet.addAll(networkInstance.getBuckets().getNearestNodes(target.getTargetIdentifier(), networkInstance.getConfiguration().getAlpha()));
		current = new HashSet<Node>();
		nextIteration();		
	}
	
	public static void search(NetworkInstance ni, FindRequest request){
		SearchHandler sh = new SearchHandler(ni, request);
	}
	
	
	void nextIteration(){
		// add the querySet to prevQueried and current and then search for every node
		while(current.size() < maxRequests){
			while(nearestSet.size() > nearestSetSize){
				nearestSet.remove(nearestSet.last());				
			}
			SortedSet<Node> unsearchedNodes = new TreeSet<Node>(new IdentifiableDistanceComparator(target.getTargetIdentifier()));
			unsearchedNodes = nearestSet;
			unsearchedNodes.removeAll(prevQueried);
			if(unsearchedNodes.isEmpty()){
				// event identifier not found
			}
			final Node nextRequest = unsearchedNodes.first();
		  	prevQueried.add(nextRequest);
		  	current.add(nextRequest);
		  	// TODO: executeFindNode
			executeFindNode(nextRequest, target, new MessageListener(){
		    	// event that a message was received
		    	void messageReceived(FindResponse result) {
		    		// if the reply contains the target then trigger identifier found
		          	if(result.found()){
		          	    //event identifier found
		          	}
		          	else{
		          		nearestSet.add(result.getNodes());
		    			networkInstance.getBuckets().addAll(result.getNodes());
		          		current.remove(nextRequest);
		          		nextIteration();
		          	}
		      	}		    	
		    	
		    	// event that the message timed out
		      	void timeOut(){
		      		current.remove(nextRequest);
		      		nextIteration();
		      	}
		    });
		}
	}
}