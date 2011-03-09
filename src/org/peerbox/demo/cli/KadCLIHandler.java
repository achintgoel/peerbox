package org.peerbox.demo.cli;

import org.peerbox.kademlia.NetworkInstance;

public class KadCLIHandler implements CLIHandler{
	NetworkInstance networkInstance;
	
	public KadCLIHandler(NetworkInstance networkInstance){
		this.networkInstance = networkInstance;
	}

	@Override
	public void handleCommand(String[] args, ExtendableCLI cli) {
		// TODO Auto-generated method stub
		
	}
	
	
}
