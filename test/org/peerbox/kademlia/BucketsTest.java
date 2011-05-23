package org.peerbox.kademlia;

import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.peerbox.kademlia.Identifier;
import org.peerbox.kademlia.NetworkInstance;
import org.peerbox.kademlia.Node;
import org.peerbox.kademlia.ResponseListener;
import org.peerbox.kademlia.messages.PingResponse;

public class BucketsTest {
	private class DummyNetworkInstance extends NetworkInstance {
		public DummyNetworkInstance(byte[] bytes) {
			super(bytes);
		}

		public void ping(Node targetNode, ResponseListener<PingResponse> responseListener) {
			Random random = new Random();
			int i = random.nextInt();
			i = i % 10;
			if (i >= 7) {
				System.out.println(targetNode.getIdentifier().getIntegerValue() + " NOT ADDED");
				responseListener.onFailure();
			} else {
				System.out.println(targetNode.getIdentifier().getIntegerValue() + " ADDED");
				responseListener.onResponseReceived(null);
			}
		}
	}

	DummyNetworkInstance dni;
	ArrayList<Node> nodePerBucket;
	ArrayList<Node> nodesInLastBucket;

	@Before
	public void setUp() throws Exception {
		byte[] bytes = new byte[20];
		for (int i = 0; i < 20; i++) {
			bytes[i] = (byte) 0x00;
		}
		bytes[0] = (byte) 0x80;
		nodePerBucket = new ArrayList<Node>();
		dni = new DummyNetworkInstance(bytes);
		nodesInLastBucket = new ArrayList<Node>();
		for (int i = 0; i < 160; i++) {
			byte[] populate = new byte[20];
			BigInteger id = new BigInteger(populate);
			id = id.flipBit(i);
			populate = id.toByteArray();
			nodePerBucket.add(new Node(null, Identifier.fromBytes(populate)));
		}
		for (int i = 0; i < 40; i++) {
			byte[] populate = new byte[20];
			BigInteger id = new BigInteger(populate);
			id = id.flipBit(i);
			System.out.println(id);
			populate = id.toByteArray();
			nodesInLastBucket.add(new Node(null, Identifier.fromBytes(populate)));
		}
	}

	@Test
	public void testGetNearestNodes() {
		fail("Not yet implemented");
	}

	@Test
	public void testCalculateBucketNumber() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddAll() {
		dni.getBuckets().addAll(nodesInLastBucket);
		for (Node node : nodesInLastBucket) {
			Node found = dni.getBuckets().findNodeByIdentifier(node.getIdentifier());
			if (found == null) {
				System.out.println(node.getIdentifier().getIntegerValue() + " not found");
			}
		}
	}

	@Test
	public void testAdd() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindNodeByIdentifier() {
		fail("Not yet implemented");
	}

}
