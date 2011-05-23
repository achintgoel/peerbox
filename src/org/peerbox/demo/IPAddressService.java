package org.peerbox.demo;

import org.peerbox.network.udp.UDPMessageServer;
import org.peerbox.rpc.RPCEvent;
import org.peerbox.rpc.RPCHandler;
import org.peerbox.rpc.RPCServiceRequestListener;
import org.peerbox.rpc.json.JsonRPCHandler;

public class IPAddressService implements RPCServiceRequestListener {

	@Override
	public void onRequestRecieved(RPCEvent e) {
		e.respond(e.getSenderURI().getHost());
	}

	public static void main(String[] args) {
		RPCHandler rpc = new JsonRPCHandler(new UDPMessageServer(20000));
		rpc.registerServiceListener("ipaddress", new IPAddressService());
	}
}
