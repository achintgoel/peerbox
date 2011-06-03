package org.peerbox.kademlia;

import java.util.List;

import org.peerbox.dht.DistributedMap;
import org.peerbox.dht.Value;
import org.peerbox.dht.ValueEvent;
import org.peerbox.dht.ValueListener;
import org.peerbox.kademlia.messages.FindValueResponse;
import org.peerbox.kademlia.messages.StoreResponse;

class PrimaryDHT implements DistributedMap<Key, Value> {
	protected final NetworkInstance networkInstance;

	PrimaryDHT(NetworkInstance networkInstance) {
		this.networkInstance = networkInstance;
	}

	/**
	 * Retrieves Value via specified Key from associated Kademlia Network via
	 * simplified Map-like interface NOTE: Retrieval failures are exposed only
	 * as if the Key does not exist in the DHT
	 * 
	 * @param key
	 * @param valueListener
	 */
	@Override
	public void get(Key key, final ValueListener<List<Value>> valueListener) {
		networkInstance.findValue(key, new ResponseListener<FindValueResponse>() {

			@Override
			public void onResponseReceived(FindValueResponse response) {
				if (response.isFound()) {
					valueListener.valueComplete(new ValueEvent<List<Value>>(response.getFoundValue()));
				} else {
					valueListener.valueComplete(new ValueEvent<List<Value>>());
				}
			}

			@Override
			public void onFailure() {
				valueListener.valueComplete(new ValueEvent<List<Value>>());
			}

		});
	}

	/**
	 * Stores Key, Value pair in associated Kademlia Network via simplified
	 * Map-like interface NOTE: Currently does not support event-handling of
	 * storage failure / acknowledgement of success Should this be implemented
	 * 
	 * @param key
	 * @param value
	 */
	@Override
	public void put(Key key, Value value) {
		networkInstance.storeValue(key, value, new ResponseListener<StoreResponse>() {
			@Override
			public void onResponseReceived(StoreResponse response) {

			}

			@Override
			public void onFailure() {

			}
		});
	}

}
