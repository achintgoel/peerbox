package org.peerbox.kademlia;

import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.peerbox.dht.CompositeDataFilter;
import org.peerbox.dht.DistributedMap;
import org.peerbox.dht.LocalDataStore;
import org.peerbox.dht.MapDataFilter;
import org.peerbox.dht.ValueListener;
import org.peerbox.kademlia.messages.FindNodeRequest;
import org.peerbox.kademlia.messages.FindNodeResponse;
import org.peerbox.kademlia.messages.FindValueRequest;
import org.peerbox.kademlia.messages.FindValueResponse;
import org.peerbox.kademlia.messages.PingRequest;
import org.peerbox.kademlia.messages.PingResponse;
import org.peerbox.kademlia.messages.Request;
import org.peerbox.kademlia.messages.Response;
import org.peerbox.kademlia.messages.StoreRequest;
import org.peerbox.kademlia.messages.StoreResponse;
import org.peerbox.rpc.RPCEvent;
import org.peerbox.rpc.RPCHandler;
import org.peerbox.rpc.RPCResponseListener;

import com.google.gson.Gson;

/**
 * An instance of a local node that is actively/inactively involved in a
 * particular Kademlia overlay network. Primary Kademlia Controller
 */
public class NetworkInstance implements Kademlia {
	protected final Buckets buckets;
	protected final Gson gson;
	protected final String rpcServiceName;
	protected final LocalDataStore localDataStore;
	protected Identifier localIdentifier;
	protected final RPCHandler rpcHandler;
	protected final CompositeDataFilter compositeDataFilter;
	protected final PrimaryDHT primaryDHT;
	protected final Configuration configuration;

	public NetworkInstance(RPCHandler rpcHandler) {
		configuration = new Configuration();
		gson = new Gson();
		rpcServiceName = "kad";
		compositeDataFilter = new CompositeDataFilter();
		localDataStore = new LocalDataStore();
		this.rpcHandler = rpcHandler;
		rpcHandler.registerServiceListener(rpcServiceName, new KademliaRequestListener(this));
		localIdentifier = Identifier.generateRandom(); // Possibly remember for
														// future restarts
		buckets = new Buckets(this);
		primaryDHT = new PrimaryDHT(this);
	}

	// TODO: this constructor only for testing of buckets
	protected NetworkInstance(byte[] bytes) {
		configuration = new Configuration();
		gson = null;
		rpcServiceName = "kad";
		compositeDataFilter = null;
		localDataStore = null;
		this.rpcHandler = null;
		localIdentifier = Identifier.fromBytes(bytes); // Possibly remember for
														// future restarts
		buckets = new Buckets(this);
		primaryDHT = new PrimaryDHT(this);
	}

	public void registerDataFilter(String primaryKey, MapDataFilter<String, String> dataFilter) {
		compositeDataFilter.registerDataFilter(primaryKey, dataFilter);
	}

	LocalDataStore getLocalDataStore() {
		return localDataStore;
	}

	CompositeDataFilter getCompositeDataFilter() {
		return compositeDataFilter;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public Identifier getLocalNodeIdentifier() {
		return localIdentifier;
	}

	public DistributedMap<Key, Value> getPrimaryDHT() {
		return primaryDHT;
	}

	public DistributedMap<String, Value> getSingleMap(final String mapKey) {
		return new DistributedMap<String, Value>() {
			@Override
			public void get(String key, ValueListener<List<Value>> vl) {
				primaryDHT.get(new Key(mapKey, key), vl);
			}

			@Override
			public void put(String key, Value value) {
				primaryDHT.put(new Key(mapKey, key), value);
			}
		};
	}

	protected <T extends Response> void sendRequestRPC(final Node destination, Request request,
			final Class<T> responseClass, final ResponseListener<T> callback) {
		String requestData = gson.toJson(request);
		getRPC().sendRequest(destination.getNetworkURI(), rpcServiceName, requestData, new RPCResponseListener() {
			public void onResponseReceived(RPCEvent event) {
				try {
					callback.onResponseReceived(gson.fromJson(event.getDataString(), responseClass));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onTimeout() {
				buckets.remove(destination);
				callback.onFailure();

			}
		});
	}

	public RPCHandler getRPC() {
		return rpcHandler;
	}

	public Buckets getBuckets() {
		return buckets;
	}

	public void findNode(Identifier targetNodeId, boolean stopOnFound,
			ResponseListener<FindNodeResponse> responseListener) {
		FindNodeRequest request = new FindNodeRequest(getLocalNodeIdentifier(), targetNodeId);
		FindProcess.execute(this, request, stopOnFound, FindNodeResponse.class, responseListener);
	}

	public void findNode(Identifier targetNodeId, ResponseListener<FindNodeResponse> responseListener) {
		findNode(targetNodeId, true, responseListener);
	}

	/**
	 * Find Value retrieves stored values in the DHT by a given key (and filters
	 * with registered data filters) If Value is invalid according to filters,
	 * it would appear not to be found. Current Assumption: If key/value pair is
	 * in local network, skip querying network for the value. Is this a valid
	 * assumption? Should it be an option?
	 * 
	 * TODO: Cache found Key/Value Pairs
	 * 
	 * @param targetKey
	 * @param responseListener
	 */
	public void findValue(final Key targetKey, final ResponseListener<FindValueResponse> responseListener) {
		List<Value> valueList = getLocalDataStore().get(targetKey);
		Date lastRefreshed = getLocalDataStore().getLastRefreshed(targetKey);
		if (valueList != null) {
			for (Value value : valueList) {
				if (!compositeDataFilter.isValid(targetKey, value.getValue())) {
					valueList.remove(value);
					getLocalDataStore().remove(targetKey, value);
					// NOTE: should we include nearby nodes since we have the
					// value locally?
				}
			}
			if (lastRefreshed != null) {
				if (lastRefreshed.after(new Date(System.currentTimeMillis()
						- (getConfiguration().getRefreshInterval() * 1000)))) {
					responseListener.onResponseReceived(new FindValueResponse(valueList, new LinkedList<Node>()));
					return;
				}
			}
		}

		FindValueRequest request = new FindValueRequest(getLocalNodeIdentifier(), targetKey);
		FindProcess.execute(this, request, true, FindValueResponse.class, new ResponseListener<FindValueResponse>() {

			@Override
			public void onResponseReceived(FindValueResponse response) {
				if (response.isFound()) {
					List<Value> foundValues = response.getFoundValue();
					for (Value value : foundValues) {
						storeValueLocal(targetKey, value, false);
					}
					getLocalDataStore().updateLastRefreshed(targetKey);
					foundValues = getLocalDataStore().get(targetKey);
					if (foundValues == null || foundValues.isEmpty()) {
						responseListener.onResponseReceived(new FindValueResponse(response.getNearbyNodes()));
						return;
					}
					responseListener.onResponseReceived(new FindValueResponse(foundValues, response.getNearbyNodes()));
				} else {
					responseListener.onResponseReceived(response);
				}
			}

			@Override
			public void onFailure() {
				responseListener.onFailure();
			}

		});
	}

	/**
	 * Stores the Key/Value pair in the local data store and then replicates the
	 * data across the network. Verifies valid according to data filters prior
	 * to storing. Calls failure callback if invalid. Replication is done via
	 * sending
	 * 
	 * TODO: Expiration and re-publication of stored values
	 * 
	 * @param key
	 * @param value
	 * @param responseListener
	 * @param publish
	 */
	public void storeValue(Key key, Value value, boolean publish, ResponseListener<StoreResponse> responseListener) {
		// TODO: do not store values more than 24 hours old
		if (!storeValueLocal(key, value, true)) {
			responseListener.onFailure();
			return;
		}
		StoreRequest request = new StoreRequest(getLocalNodeIdentifier(), key, value);
		StoreProcess.execute(this, request, responseListener);
	}

	/**
	 * Stores Key/Value pair in local data store if pair passes filtering.
	 * 
	 * @param key
	 * @param value
	 * @return Whether store succeeded (filtering passed)
	 */
	public boolean storeValueLocal(Key key, Value value, boolean original) {
		if (!compositeDataFilter.isValid(key, value.getValue())) {
			return false;
		}
		int closerNodes = buckets.getCloserNodeCount(key);
		int expiryTime = configuration.getMaxExpiry();
		if(closerNodes > configuration.getK()){
			expiryTime *= Math.exp(configuration.getK()/closerNodes);
			if(expiryTime < configuration.getMinExpiry())
				expiryTime = configuration.getMinExpiry();
		}
		getLocalDataStore().put(key, value, original, expiryTime * 1000);		
		return true;
	}

	// Maybe put in Node
	public void ping(final Node targetNode, final ResponseListener<PingResponse> responseListener) {
		PingRequest request = new PingRequest(getLocalNodeIdentifier(), targetNode.getIdentifier());
		this.sendRequestRPC(targetNode, request, PingResponse.class, new ResponseListener<PingResponse>() {
			@Override
			public void onResponseReceived(PingResponse response) {
				if (response.getMyNodeId() == null) {
					responseListener.onFailure();
				}
				if (targetNode.isComplete() && !response.getMyNodeId().equals(targetNode.getIdentifier())) {
					responseListener.onFailure();
				} else {
					responseListener.onResponseReceived(response);
				}
			}

			@Override
			public void onFailure() {
				responseListener.onFailure();
			}
		});
	}

	public void bootstrap(List<URI> friends, BootstrapListener bootstrapListener) {
		BootstrapProcess.execute(this, friends, bootstrapListener);
	}
}
