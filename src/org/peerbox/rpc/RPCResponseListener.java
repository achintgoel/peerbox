package org.peerbox.rpc;

public interface RPCResponseListener {
	public void onResponseReceived(RPCEvent event);
	public void onTimeout();
}
