package org.peerbox.demo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.peerbox.kademlia.BootstrapListener;
import org.peerbox.kademlia.Kademlia;
import org.peerbox.kademlia.NetworkInstance;
import org.peerbox.network.udp.UDPMessageServer;
import org.peerbox.rpc.RPCHandler;
import org.peerbox.rpc.json.JsonRPCHandler;

public class KadInstance implements Runnable {
	private String bindIP;
	private int bindPort;
	private RPCHandler rpc;
	private List<URI> bootstrapURI;
	private Kademlia networkInstance;

	public KadInstance(String bindIP, int bindPort, List<URI> bootstrapURI) {
		this.bindIP = bindIP;
		this.bindPort = bindPort;
		this.bootstrapURI = bootstrapURI;
		rpc = new JsonRPCHandler(new UDPMessageServer(bindPort));
		try {
			rpc.setLocalURI(new URI("udp://" + bindIP + ":" + bindPort));
		} catch (URISyntaxException e) {
			System.out.println("Illegal URI Syntax");
		}
	}

	public KadInstance(RPCHandler rpc, String bindIP, int bindPort, List<URI> bootstrapURI) {
		this.rpc = rpc;
		this.bindPort = bindPort;
		this.bindIP = bindIP;
		this.bootstrapURI = bootstrapURI;
	}

	public void run() {
		if (bootstrapURI.isEmpty()) {
			System.out.println("Node started successfully at " + bindIP + ":" + bindPort);
			return;
		}
		NetworkInstance.startNetworkInstance(this.rpc, bootstrapURI, new BootstrapListener() {

			@Override
			public void onBootstrapFailure() {
				System.out.println("Node bootstrap at " + bindIP + ":" + bindPort + " failed");
			}

			@Override
			public void onBootstrapSuccess(Kademlia kad) {
				networkInstance = kad;
				System.out.println("Node started successfully at " + bindIP + ":" + bindPort);
			}
		});
	}

	public URI getURI() {
		return rpc.getLocalURI();
	}
}
