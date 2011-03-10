package org.peerbox.demo;

import org.peerbox.rpc.RPCEvent;
import org.peerbox.rpc.RPCHandler;
import org.peerbox.rpc.ServiceRequestListener;

public class IPAddressService implements ServiceRequestListener {

	@Override
	public void onRequestRecieved(RPCEvent e) {
		e.respond(e.getSenderURI().getHost());
	}
	
	public static void main(String[] args) {
		RPCHandler rpc = RPCHandler.getUDPInstance(20000);
		rpc.registerServiceListener("ipaddress", new IPAddressService());
	}
}
