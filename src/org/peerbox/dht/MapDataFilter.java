package org.peerbox.dht;

/**
 * Used to filter data stored in a Map or other key/value storage
 *
 * @param <K> Map Key Type
 * @param <V> Map Value Type
 */
public interface MapDataFilter<K, V> {
	/**
	 * Determined whether the specified key/value pair is a valid entry to store in the map
	 * @param key
	 * @param value
	 * @return True if Valid, False if not Valid
	 */
	public boolean isValid(K key, V value);
}
