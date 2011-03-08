package org.peerbox.testlets;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.LinkedList;
import java.util.Scanner;

import org.peerbox.friendpeer.FriendManager;
import org.peerbox.kademlia.BootstrapListener;
import org.peerbox.kademlia.NetworkInstance;
import org.peerbox.rpc.RPCHandler;
import org.peerbox.security.SecureMessageHandler;


public class FileShareTest {
	public static void main(String[] args) {
		int port;
		if (args.length < 1) {
			System.out.println("error, please specify port");
			return;
		} else {
			port = Integer.parseInt(args[0]);
		}
		LinkedList<URI> startURIs = new LinkedList<URI>();
		for (int i = 1; i < args.length; i++) {
			try {
				startURIs.add(new URI("udp://localhost:" + args[i]));
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		final NetworkInstance instance = new NetworkInstance(RPCHandler.getUDPInstance(port));
		System.out.println("Network instance created: " + instance.getLocalNodeIdentifier());

		if (startURIs.size() > 0) {
			instance.bootstrap(startURIs, new BootstrapListener() {
	
				@Override
				public void onBootstrapSuccess() {
					System.out.println("Bootstrap Complete");
				}
	
				@Override
				public void onBootstrapFailure() {
					System.out.println("Bootstrap Failed");	
				}
			});
		}
		final FriendManager manager = new FriendManager(instance.getSingleMap("users"), new SecureMessageHandler());
		manager.signOn(port);
		System.out.println("signed on properly");
		Scanner scan = new Scanner(System.in);
		int count = 0;
		while(true){
			try{
				String function = scan.next();
				if(function.equals("addFriend") && scan.hasNextBigInteger()) {
					X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(scan.nextBigInteger().toByteArray());
					 KeyFactory keyFactory = KeyFactory.getInstance("DSA");
					 PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
					 manager.createFriend("newFriend"+count, null, pubKey);
					 count++;
				}
				if(function.equals("printBuddys")) {
					manager.printBuddyList();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
