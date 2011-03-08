package org.peerbox.testlets;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

import org.peerbox.kademlia.BootstrapListener;
import org.peerbox.kademlia.NetworkInstance;
import org.peerbox.rpc.RPCHandler;


public class KadTestInstance {

	/**
	 * @param args
	 */
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
				e.printStackTrace();
			}
		}
		new KadTestInstance(port, startURIs);
	}
	
	public KadTestInstance(int port, LinkedList<URI> startURIs) {
		NetworkInstance instance = new NetworkInstance(RPCHandler.getUDPInstance(port++));
		System.out.println("Network instance created");

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
	}

}
