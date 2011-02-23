package dht;

import java.io.Serializable;

public interface DistributedMap<K extends Serializable, V extends Serializable> {
	public void get(K key, ValueListener<V> vl);
	public void put(K key, V value);
}
