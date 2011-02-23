package kademlia;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;
import java.util.HashSet;

import javax.swing.Timer;


public class SearchHandler {
	Set<Node> prevQueried;
	Set<Node> querySet;
	SortedSet<Node> foundNodesSet;
	Set<Node> current;
	Identifier targetID;

	void searchInit(Identifier target){
		targetID = target;
		querySet = new HashSet<Node>(getNearestNodes(targetID, alpha));
		prevQueried = new HashSet<Node>();
	}
	
	
	void searchQuerySet(){
	  	prevQueried.addAll(querySet);
	  	current.addAll(querySet);
	  	for (Node node : querySet) {
		    //in parallel, event based
		    executeFindNode(node, targetID, new MessageListener(current) {
		    	void messageReceived(FindNodeResult result) {
		          	if(result.found()){
		          	    //event identifier found
		          	}
		          	else{
		          		//trigger updatePrevQueried
		          	}
		          	if(qo.size() == 0){
		          		//trigger build and search
		          	}
		      	}
		      	void timeOut(){
					//trigger final search step
		      	}
		    });
		}
	  	ActionListener halfTimeoutListener = new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub				
			}
	  	};
	    Timer halfTimer = new Timer(halfTimeout, halfTimeoutListener); 
	}
	
	void updatePrevQueried(FindNodeResults fnr){	
	    foundNodesSet.addAll(fnr.nodes());
		NetworkInstance.getBuckets().addAll(foundNodesSet);
	}
	
	void buildNewQuerySet(){
		Set<Node> newQuerySet = new HashSet((new ArrayList(foundNodesSet)).subList(0, foundNodesSet.size() > alpha ? alpha : foundNodesSet.size()));
		newQuerySet.removeAll(prevQueried);
		querySet = newQuerySet;
		if (querySet.size() == 0) {
			//call finalSearchStep
		}
	}

	void finalSearchStep(){
		querySet = new HashSet<Node>(getNearestNodes(targetID, k);
	}
}
