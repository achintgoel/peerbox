package kademlia;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;
import java.util.HashSet;
import java.util.TreeSet;

import javax.swing.Timer;


public class SearchHandler {
	Set<Node> prevQueried;				// previously queried nodes
	Set<Node> querySet;					// nodes to be queried in the next search
	SortedSet<Node> totalFoundSet;		// nodes that have been found through all searches
	SortedSet<Node> currentFoundSet;	// nodes found in current search
	Set<Node> current;					// nodes currently being queried but haven't replied
	Identifier targetID;				// node being searched for
	boolean oneReply;					// true if at least one node from current replied

	
	
	public SearchHandler(Identifier target){
		targetID = target;
		querySet = new HashSet<Node>(getNearestNodes(targetID, alpha));
		prevQueried = new HashSet<Node>();
		oneReply = false;
		totalFoundSet = new TreeSet<Node>(new IdentifiableDistanceComparator(targetID));
		currentFoundSet = new TreeSet<Node>(new IdentifiableDistanceComparator(targetID));
		searchQuerySet();
	}
	
	
	void searchQuerySet(){
		// add the querySet to prevQueried and current and then search for every node
	  	prevQueried.addAll(querySet);
	  	current.addAll(querySet);
	  	for (Node node : querySet) {
		    executeFindNode(node, targetID, new MessageListener(){
		    	// event that a message was received
		    	void messageReceived(FindNodeResult result) {
		    		// if the reply contains the target then trigger identifier found
		          	if(result.found()){
		          	    //event identifier found
		          	}
		          	// otherwise updatePrev
		          	else{
		          		updateCurrent(result);
		          	}
		      	}
		    	
		    	// event that the message timed out
		      	void timeOut(){
					updateCurrent(new FindNodeResult(timeout));
		      	}
		    });
		}
	  	// then start a timer for half timeout
	    Timer halfTimer = new Timer(halfTimeout, new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				updateCurrent(new FindNodeResult(halfTime));
			}
	  	}); 
	}
	
	
	void updateCurrent(FindNodeResult fnr){
		// if request timed out
		if(fnr.timeOut()){
			// remove request from current
			current.remove(fnr.getSender());
			if(!currentFoundSet.isEmpty()){
				
			}
			// if no active searches
			if(current.isEmpty()){
				// if no results have been found in the last alpha requests
				if(currentFoundSet.isEmpty()){
					finalSearchStep();
				}
				// if there have been results then build the new query set and search
				else{
					buildNewQuerySet();
				}
			}
		}
		// if there is a half timeout
		else if(fnr.halfTimeOut()){
			if(!currentFoundSet.isEmpty()){
				buildNewQuerySet();
			}
		}
		// otherwise there is a reply
		else{
			current.remove(fnr.getSender());
			currentFoundSet.addAll(fnr.nodes());
			totalFoundSet.addAll(fnr.nodes());
			NetworkInstance.getBuckets().addAll(fnr.nodes());
			if(current.isEmpty()){
				buildNewQuerySet();
			}
		}
	}
	
	// 
	void buildNewQuerySet(){
		Set<Node> newQuerySet = new HashSet<Node>((new ArrayList<Node>(currentFoundSet)).subList(0, currentFoundSet.size() > alpha ? alpha : currentFoundSet.size()));
		currentFoundSet = new TreeSet<Node>(new IdentifiableDistanceComparator(targetID));
		newQuerySet.removeAll(prevQueried);
		querySet = newQuerySet;
		//if no new results
		if (querySet.size() == 0) {
			finalSearchStep();
		}
		else{
			searchQuerySet();
		}
	}

	void finalSearchStep(){
		querySet = new HashSet<Node>(getNearestNodes(targetID, k));
	}
}
