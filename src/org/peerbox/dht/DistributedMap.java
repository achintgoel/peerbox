package org.peerbox.dht;


public interface DistributedMap<K, V> {
	public void get(K key, ValueListener<V> vl);
	public void put(K key, V value);
}
