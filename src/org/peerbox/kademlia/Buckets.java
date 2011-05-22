package org.peerbox.kademlia;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.peerbox.kademlia.messages.PingResponse;



/**
 * 
 * Object that holds the k-buckets routing table for the known nodes in Kademlia network
 *
 */
public class Buckets implements NodeStatusListener {
	protected List<LinkedHashMap<Identifier, Node>> buckets;
	final protected Kademlia networkInstance;
	final int k;
	
	
	/**
	 * Constructor
	 * @param instance
	 */
	public Buckets(Kademlia instance) {
		networkInstance = instance;
		k = networkInstance.getConfiguration().getK();
		int b = networkInstance.getConfiguration().getB();
		buckets = new ArrayList<LinkedHashMap<Identifier, Node>>(b);
		for (int i = 0; i < b; i++) {
			buckets.add(i, new LinkedHashMap<Identifier, Node>());
		}
	}
	
	/**
	 * Should be called when a node is detected to be offline
	 */
	public void onNodeDown(Node node) {
		remove(node);
	}
	
	/**
	 * Called when a node is found online
	 */
	public void onNodeAlive(Node node) {
		add(node); 
	}
	
	
	/**
	 * Find the closest nodes in buckets routing table to the given node
	 * @param id ID of the target node
	 * @param numberOfNodes number of results to return
	 * @return List<Node>
	 */
	public List<Node> getNearestNodes(Identifier id, int numberOfNodes) {
		int bucketNumber = calculateBucketNumber(id);
		
		SortedSet<Node> nearSet = new TreeSet<Node>(new IdentifiableDistanceComparator(id));
		
		nearSet.addAll(buckets.get(bucketNumber).values());
		
		int left = bucketNumber - 1;
		int right = bucketNumber + 1;
		while (nearSet.size() < numberOfNodes && (left >= 0 || right < buckets.size())) {
			if (left >= 0) {
				nearSet.addAll(buckets.get(left--).values());
			}
			if (right < buckets.size() && nearSet.size() < numberOfNodes) {
				nearSet.addAll(buckets.get(right++).values());
			}
		}
		
		List<Node> nearest = new ArrayList<Node>(nearSet);
		return nearest.subList(0, nearest.size() > numberOfNodes ? numberOfNodes : nearest.size());
	}
	
	/**
	 * Return the number of nodes in a given bucket
	 * @param bucket
	 * @return
	 */
	public int getBucketNodeCount(int bucket) {
		return buckets.get(bucket).size();
	}
	
	/**
	 * To calculate the bucket number a Node or other identifiable object will fall in
	 * @param obj
	 * @return
	 */
	public int calculateBucketNumber(Identifiable obj) {		
		if (obj.getIdentifier().equals(networkInstance.getLocalNodeIdentifier())) {
			return 0;
		}
		
		int bucketNum =  (int) Math.floor(Math.log(Identifier.calculateDistance(
				networkInstance.getLocalNodeIdentifier(), obj).doubleValue())/Math.log(2));
		return bucketNum;
	}
	
	/**
	 * to add a collection of nodes to the buckets routing table
	 * @param nodes Collection of Nodes
	 */
	public void addAll(Collection<Node> nodes) {
		for (Node node : nodes) {
			add(node);
		}		
	}

	/**
	 * To add a single Node to the buckets routing table
	 * @param newNode
	 */
	public void add(final Node newNode) {
		if (!newNode.isComplete()) {
			return;
		}
		if (newNode.getIdentifier().equals(networkInstance.getLocalNodeIdentifier())) {
			return;
		}
		
		int bucketNumber = calculateBucketNumber(newNode.getIdentifier());
		final LinkedHashMap<Identifier, Node> currentBucket = buckets.get(bucketNumber);
		if(currentBucket.containsKey(newNode.getIdentifier())){
			// Move existing node to the front
			currentBucket.remove(newNode.getIdentifier());
			currentBucket.put(newNode.getIdentifier(), newNode);
		}
		else {
			if (currentBucket.size() < k) {
				// Add new node
				currentBucket.put(newNode.getIdentifier(), newNode);
			} else {
				// gets the first element from the LinkedHashMap
				final Node firstNode = currentBucket.entrySet().iterator().next().getValue();
				networkInstance.ping(firstNode, new ResponseListener<PingResponse>(){
					@Override
					public void onFailure() {
						currentBucket.put(newNode.getIdentifier(), newNode);
					}
					@Override
					public void onResponseReceived(PingResponse response) {
						currentBucket.get(firstNode.getIdentifier());
					}					
				});
			}
		}
	}
	
	/**
	 * To find a Node by its Identifier
	 * @param identifier
	 * @return Node
	 */
	public Node findNodeByIdentifier(Identifier identifier) {
		int bucketNumber = calculateBucketNumber(identifier);
		return buckets.get(bucketNumber).get(identifier);
	}
	
	//TODO: Remove
	public void print(PrintStream out){
		for (int i = 0; i < buckets.size(); i++) {
			LinkedHashMap<Identifier, Node> bucket = buckets.get(i);
			if (!bucket.isEmpty()) {
				out.println("Bucket No : " + i);
				for (Entry<Identifier, Node> entry : bucket.entrySet()) {
					out.println("   ID = " + entry.getKey() + ",\t\t URI = " + (entry.getValue()).getNetworkURI());
				}
				out.println();
			}
		}
	}
	
	public void remove(Node node){
		if (!node.isComplete()) {
			return;
		}
		buckets.get(calculateBucketNumber(node)).remove(node.getIdentifier());
	}
	
	public Identifier getNearestNode(Identifiable obj){
		int bucketNum = calculateBucketNumber(obj);
		SortedSet<Node> nearSet = new TreeSet<Node>(new IdentifiableDistanceComparator(obj));	
		nearSet.addAll(buckets.get(bucketNum).values());
		if(nearSet == null || nearSet.isEmpty()){
			return null;
		}
		return nearSet.first().getIdentifier();
	}
	
	
	/**
	 * To get the count of the nodes between the current node and
	 *  the node closest to obj
	 * @param obj - 
	 * @return the number of notes
	 */
	public int getCloserNodeCount(Identifiable obj){
		int bucketNum = calculateBucketNumber(obj);
		int nodeCount = 0;
		for(int i = 0; i < bucketNum ; i++){
			nodeCount += buckets.get(i).size();
		}
		Identifier closestNode = getNearestNode(obj);
		for(Identifier id : buckets.get(bucketNum).keySet()){
			if(Identifier.calculateDistance(networkInstance.getLocalNodeIdentifier(), closestNode).compareTo(Identifier.calculateDistance(networkInstance.getLocalNodeIdentifier(), id)) == 1){
				nodeCount++;
			}
		}
		return nodeCount;
	}
}
