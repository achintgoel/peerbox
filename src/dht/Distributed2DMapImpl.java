package dht;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Map;

public class Distributed2DMapImpl<K extends Serializable> implements Distributed2DMap<K>, DistributedMap<CompositeKey<K, Serializable>, Serializable> {
	
	protected DistributedMap<CompositeKey<K, Serializable>, Serializable> baseMap;
	
	public Distributed2DMapImpl(DistributedMap<CompositeKey<K, Serializable>, Serializable> baseMap) {
		if (baseMap == null) {
			throw new InvalidParameterException();
		}
		this.baseMap = baseMap;
	}
	
	@Override
	public void provideAlternateMapStore(Serializable mapKey, Map map) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void provideAlternateDistributedMap(Serializable mapKey, DistributedMap distributedMap) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public DistributedMap<Serializable, Serializable> getMap(K mapKey) {
		return new SingleMapView(mapKey);
	}
	
	@Override
	public void get(CompositeKey<K, Serializable> key, ValueListener<Serializable> vl) {
		baseMap.get(key, vl);
	}

	@Override
	public void put(CompositeKey<K, Serializable> key, Serializable value) {
		baseMap.put(key, value);
	}

	private class SingleMapView implements DistributedMap<Serializable, Serializable> {
		protected K primaryKey;
		
		protected SingleMapView(K primaryKey) {
			this.primaryKey = primaryKey;
		}
		
		@Override
		public void get(Serializable key, ValueListener<Serializable> vl) {
			Distributed2DMapImpl.this.get(new CompositeKey<K, Serializable>(primaryKey, key), vl);
		}

		@Override
		public void put(Serializable key, Serializable value) {
			Distributed2DMapImpl.this.put(new CompositeKey<K, Serializable>(primaryKey, key), value);
		}
	}


	
}
