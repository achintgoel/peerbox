package kademlia;

import rpc.RPCEvent;
import rpc.ServiceRequestListener;

public class KademliaRequestListener implements ServiceRequestListener {

	@Override
	public void onRequestRecieved(RPCEvent e) {
		e.getDataString();
		
	}

}
