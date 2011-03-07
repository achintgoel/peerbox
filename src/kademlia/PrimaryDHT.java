package kademlia;

import kademlia.messages.FindValueResponse;
import kademlia.messages.StoreResponse;
import dht.DistributedMap;
import dht.ValueEvent;
import dht.ValueListener;

class PrimaryDHT implements DistributedMap<Key, String> {
	protected final NetworkInstance networkInstance;
	
	PrimaryDHT(NetworkInstance networkInstance) {
		this.networkInstance = networkInstance;
	}
	
	
	/**
	 * Retrieves Value via specified Key from associated Kademlia Network via simplified Map-like interface
	 * NOTE: Retrieval failures are exposed only as if the Key does not exist in the DHT
	 * @param key
	 * @param valueListener
	 */
	@Override
	public void get(Key key, final ValueListener<String> valueListener) {
		networkInstance.findValue(key, new ResponseListener<FindValueResponse>() {

			@Override
			public void onResponseReceived(FindValueResponse response) {
				if (response.isFound()) {
					valueListener.valueComplete(new ValueEvent<String>(response.getFoundValue()));
				} else {
					valueListener.valueComplete(new ValueEvent<String>());
				}	
			}

			@Override
			public void onFailure() {
				valueListener.valueComplete(new ValueEvent<String>());
			}
			
		});
	}

	/**
	 * Stores Key, Value pair in associated Kademlia Network via simplified Map-like interface
	 * NOTE: Currently does not support event-handling of storage failure / acknowledgement of success
	 * Should this be implemented
	 * @param key
	 * @param value
	 */
	@Override
	public void put(Key key, String value) {
		networkInstance.storeValue(key, value, true, new ResponseListener<StoreResponse>() {
			@Override
			public void onResponseReceived(StoreResponse response) {
				
			}

			@Override
			public void onFailure() {
				
			}
		});
	}

}
