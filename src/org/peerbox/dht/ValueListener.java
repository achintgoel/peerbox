package org.peerbox.dht;


public interface ValueListener<V> {
	public void valueComplete(ValueEvent<V> valueEvent);
}