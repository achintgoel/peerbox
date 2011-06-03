package org.peerbox.kademlia;

import org.peerbox.dht.DistributedMap;
import org.peerbox.dht.MapDataFilter;
import org.peerbox.dht.Value;
import org.peerbox.kademlia.messages.FindNodeResponse;
import org.peerbox.kademlia.messages.FindValueResponse;
import org.peerbox.kademlia.messages.PingResponse;
import org.peerbox.kademlia.messages.StoreResponse;

public interface Kademlia {

	void registerDataFilter(String primaryKey, MapDataFilter<String, String> dataFilter);

	Identifier getLocalNodeIdentifier();

	Configuration getConfiguration();

	Buckets getBuckets();

	DistributedMap<Key, Value> getPrimaryDHT();

	DistributedMap<String, Value> getSingleMap(String mapKey);

	void findNode(Identifier targetNodeId, ResponseListener<FindNodeResponse> responseListener);

	void findValue(Key targetKey, ResponseListener<FindValueResponse> responseListener);
	
	void storeValue(Key key, Value value, ResponseListener<StoreResponse> responseListener);

	void ping(Node targetNode, ResponseListener<PingResponse> responseListener);


}
