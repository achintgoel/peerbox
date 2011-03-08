package org.peerbox.testlets;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

import org.peerbox.kademlia.BootstrapListener;
import org.peerbox.kademlia.NetworkInstance;
import org.peerbox.rpc.RPCHandler;


public class KadThisBetterWork implements Runnable {
	static int port;
	static boolean first;
	static int successes;
	static URI lastURI;
	
	public static void main(String[] args) {
		port = 7012;
		first = true;
		successes = 0;
		
		
		
		for (int i = 0; i < 100; i++) {
			try {
				Thread.sleep(550);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			new KadThisBetterWork().run();
		}
		
	}

	@Override
	public void run() {
		RPCHandler handler = RPCHandler.getUDPInstance(port++);
		NetworkInstance instance = new NetworkInstance(handler);
		if (!first) {
			LinkedList<URI> startURIs = new LinkedList<URI>();
			startURIs.add(lastURI);
			System.out.println("Next node created " + port);
			instance.bootstrap(startURIs, new BootstrapListener() {
	
				@Override
				public void onBootstrapSuccess() {
					System.out.println("Next Node Bootstrap Complete");
					successes++;
					System.out.println("successes = " + successes);
					if (successes == 999) {
						System.out.println("They all bootstrapped successfully!!!!!!");
					}
					
				}
	
				@Override
				public void onBootstrapFailure() {
					System.out.println("Next Node Bootstrap Failed");
					
				}
				
			});
		} else {
			first = false;
		}
		lastURI = handler.getLocalURI();
	}
}