package dht;

import java.util.HashMap;
import java.util.Map;

/**
 * Dump data storage
 * TODO: Replace with standard map.
 * NOTE: This does NOT do any filtering.
 * @author rajiv
 *
 */
public class LocalDataStore {
	protected final Map<CompositeKey<String, String>, String> dataMap;
	
	public LocalDataStore() {
		dataMap = new HashMap<CompositeKey<String, String>, String>();
	}
	
	public void put(CompositeKey<String, String> ckey, String value) {
		dataMap.put(ckey, value);
	}
	
	public String get(CompositeKey<String, String> ckey) {
		return dataMap.get(ckey);
	}
}
