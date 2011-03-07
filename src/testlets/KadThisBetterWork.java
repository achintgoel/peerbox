package testlets;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

import kademlia.BootstrapListener;
import kademlia.NetworkInstance;
import rpc.RPCHandler;

public class KadThisBetterWork implements Runnable {
	static int port;
	static boolean first;
	static int successes;
	
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
			new Thread(new KadThisBetterWork()).run();
		}
		
	}

	@Override
	public void run() {
		NetworkInstance instance = new NetworkInstance(RPCHandler.getUDPInstance(port++));
		if (!first) {
			LinkedList<URI> startURIs = new LinkedList<URI>();
			try {
				startURIs.add(new URI("udp://localhost:" + (port - 2)));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			System.out.println("Next node created " + port);
			instance.bootstrap(startURIs, new BootstrapListener() {
	
				@Override
				public void onBootstrapSuccess() {
					System.out.println("Next Node Bootstrap Complete");
					successes++;
					System.out.println("successes = " + successes);
					if (successes == 99) {
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
	}
}
