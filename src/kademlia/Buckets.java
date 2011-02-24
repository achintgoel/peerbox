package kademlia;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Buckets implements NodeStatusListener {
	protected List<LinkedHashMap<InetSocketAddress, Node>> buckets;
	final protected NetworkInstance networkInstance;
	final int k;
	
	public Buckets(NetworkInstance instance) {
		networkInstance = instance;
		k = networkInstance.getConfiguration().getK();
		buckets = new ArrayList<LinkedHashMap<InetSocketAddress,Node>>(networkInstance.getConfiguration().getB());
		for (int i = 0; i < k; i++) {
			buckets.add(i, new LinkedHashMap<InetSocketAddress, Node>());
		}
	}
	
	public void onNodeDown(Node node) {
		buckets.get(calculateBucketNumber(node)).remove(node);
	}
	
	public void onNodeAlive(Node node) {
		int bucketNumber = calculateBucketNumber(node);
		LinkedHashMap<InetSocketAddress, Node> bucket = buckets.get(bucketNumber);
		if (bucket.containsKey(node.getAddress())) {
			// Move existing node to the front
			bucket.remove(node.getAddress());
			bucket.put(node.getAddress(), node);
		} else {
			if (bucket.size() < k) {
				// Add new node
				bucket.put(node.getAddress(), node);
			} else {
				//TODO: Ping last seen node and see if it responds
				//      If it does, move to back. If not, add new 
				//      node to the back
			}
		}
	}
	
	public List<Node> getNearestNodes(Identifier id, int numberOfNodes) {
		int bucketNumber = calculateBucketNumber(id);
		
		SortedSet<Node> nearSet = new TreeSet<Node>(new IdentifiableDistanceComparator(id));
		
		nearSet.addAll(buckets.get(bucketNumber).values());
		
		int left = bucketNumber - 1;
		int right = bucketNumber + 1;
		while (nearSet.size() < numberOfNodes) {
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
	
	public int getBucketNodeCount(int bucket) {
		return buckets.get(bucket).size();
	}
	
	public int calculateBucketNumber(Identifiable obj) {
		return (int) Math.floor(Math.log(Identifier.calculateDistance(
				networkInstance.getLocalNodeIdentifier(), obj).doubleValue())/Math.log(2));
	}

	public void addAll(Collection<Node> nodes) {
		// TODO Auto-generated method stub
		
	}
}
