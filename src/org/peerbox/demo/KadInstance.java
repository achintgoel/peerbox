package org.peerbox.demo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.peerbox.kademlia.BootstrapListener;
import org.peerbox.kademlia.NetworkInstance;
import org.peerbox.rpc.RPCHandler;

public class KadInstance implements Runnable {
	private String bindIP;
	private int bindPort;
	private RPCHandler rpc;
	private List<URI> bootstrapURI;
	private NetworkInstance networkInstance;
	
	public KadInstance(String bindIP, int bindPort, List<URI> bootstrapURI){
		this.bindIP = bindIP;
		this.bindPort = bindPort;
		this.bootstrapURI = bootstrapURI;
		rpc = RPCHandler.getUDPInstance(bindPort);
		try {
			rpc.setLocalURI(new URI("udp://" + bindIP + ":" + bindPort));
		} catch (URISyntaxException e) {
			System.out.println("Illegal URI Syntax");
		}
		networkInstance = new NetworkInstance(rpc);
	}

	public KadInstance(RPCHandler rpc, String bindIP, int bindPort,	List<URI> bootstrapURI) {
		this.rpc = rpc;
		this.bindPort = bindPort;
		this.bindIP = bindIP;
		this.bootstrapURI = bootstrapURI;
		networkInstance = new NetworkInstance(rpc);
	}

	public void run() {
		if(bootstrapURI.isEmpty()){
			System.out.println("Node started successfully at " + bindIP + ":" + bindPort);
			return;
		}
		networkInstance.bootstrap(bootstrapURI, new BootstrapListener(){

			@Override
			public void onBootstrapFailure() {
				System.out.println("Node bootstrap at " + bindIP + ":" + bindPort + " failed");
			}

			@Override
			public void onBootstrapSuccess() {
				System.out.println("Node started successfully at " + bindIP + ":" + bindPort);				
			}			
		});
	}
	
	public URI getURI(){
		return rpc.getLocalURI();
	}
}
