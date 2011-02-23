package dht;

import java.io.Serializable;
import java.util.Map;

public interface Distributed2DMap<K extends Serializable> {
	public void provideAlternateMapStore(K mapKey, Map<? extends Serializable, ? extends Serializable> map);
	public void provideAlternateDistributedMap(K mapKey, DistributedMap<?, ?> distributedMap);
	public DistributedMap<?, ?> getMap(K mapKey);
	
}