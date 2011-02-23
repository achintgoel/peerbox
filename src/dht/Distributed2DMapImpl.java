package dht;

import java.security.InvalidParameterException;
import java.util.Map;

public class Distributed2DMapImpl<PrimaryKeyType> implements Distributed2DMap<PrimaryKeyType>, DistributedMap<CompositeKey<PrimaryKeyType, Object>, Object> {
	
	protected DistributedMap<CompositeKey<PrimaryKeyType, Object>, Object> baseMap;
	
	public Distributed2DMapImpl(DistributedMap<CompositeKey<PrimaryKeyType, Object>, Object> baseMap) {
		if (baseMap == null) {
			throw new InvalidParameterException();
		}
		this.baseMap = baseMap;
	}
	
	@Override
	public void provideAlternateMapStore(PrimaryKeyType mapKey, Map map) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void provideAlternateDistributedMap(PrimaryKeyType mapKey, DistributedMap distributedMap) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public DistributedMap<Object, Object> getMap(PrimaryKeyType mapKey) {
		return new SingleMapView(mapKey);
	}
	
	@Override
	public void get(CompositeKey<PrimaryKeyType, Object> key, ValueListener<Object> vl) {
		baseMap.get(key, vl);
	}

	@Override
	public void put(CompositeKey<PrimaryKeyType, Object> key, Object value) {
		baseMap.put(key, value);
	}

	private class SingleMapView implements DistributedMap<Object, Object> {
		protected PrimaryKeyType primaryKey;
		
		protected SingleMapView(PrimaryKeyType primaryKey) {
			this.primaryKey = primaryKey;
		}
		
		@Override
		public void get(Object key, ValueListener<Object> vl) {
			Distributed2DMapImpl.this.get(new CompositeKey<PrimaryKeyType, Object>(primaryKey, key), vl);
		}

		@Override
		public void put(Object key, Object value) {
			Distributed2DMapImpl.this.put(new CompositeKey<PrimaryKeyType, Object>(primaryKey, key), value);
		}
	}


	
}
