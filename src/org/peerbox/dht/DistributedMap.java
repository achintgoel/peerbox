package org.peerbox.dht;

import java.util.List;

public interface DistributedMap<K, V> {
	public void get(K key, ValueListener<List<V>> vl);

	public void put(K key, V value);
}
