package dht;

import java.util.Map;

public interface Distributed2DMap<K> {
	public void provideAlternateMapStore(K mapKey, Map<?, ?> map);
	public void provideAlternateDistributedMap(K mapKey, DistributedMap<?, ?> distributedMap);
	public DistributedMap<?, ?> getMap(K mapKey);
	
}