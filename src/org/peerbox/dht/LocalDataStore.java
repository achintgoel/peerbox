package org.peerbox.dht;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.peerbox.kademlia.Value;

/**
 * Dump data storage
 * TODO: Replace with standard map.
 * NOTE: This does NOT do any filtering.
 * @author rajiv
 *
 */
public class LocalDataStore {
	
	protected class StoredValueInfo{
		public Date publishDate;
		public Date lastRepublish;
		public boolean original;
		
		StoredValueInfo(Date publishDate, boolean original){
			this.publishDate = publishDate;
			this.original = original;
		}
	}
	
	protected final Map<CompositeKey<String, String>, HashMap<String, StoredValueInfo>> dataMap;
	protected final Map<CompositeKey<String, String>, Date> lastRefreshed;
	
	public LocalDataStore() {
		dataMap = new HashMap<CompositeKey<String, String>, HashMap<String, StoredValueInfo>>();
		lastRefreshed = new HashMap<CompositeKey<String, String>, Date>();
	}
	
	public void put(CompositeKey<String, String> ckey, Value value, boolean originalPublisher) {
		HashMap<String, StoredValueInfo> valueMap;
		if(dataMap.containsKey(ckey)){
			valueMap = dataMap.get(ckey);
			if(valueMap.containsKey(value.getValue())){
				Date originaldate = valueMap.get(value.getValue()).publishDate;
				if(originaldate.before(value.getTimestamp())){
					valueMap.remove(value.getValue());
				}
				else{
					return;
				}
			}
		}
		else{
			valueMap = new HashMap<String, StoredValueInfo>();
			dataMap.put(ckey, valueMap);
		}
		valueMap.put(value.getValue(), new StoredValueInfo(value.getTimestamp(), originalPublisher));
	}
	
	
	public void updateLastRefreshed(CompositeKey<String, String> ckey){
		if(!dataMap.containsKey(ckey)){
			return;
		}
		if(lastRefreshed.containsKey(ckey)){
			lastRefreshed.remove(ckey);
		}
		lastRefreshed.put(ckey, new Date(System.currentTimeMillis()));
	}
	
	
	public LinkedList<Value> get(CompositeKey<String, String> ckey) {
		if(!dataMap.containsKey(ckey)){
			return null;
		}
		LinkedList<Value> returnList = new LinkedList<Value>();
		for(Entry<String, StoredValueInfo> entry : dataMap.get(ckey).entrySet()){
			returnList.add(new Value(entry.getKey(), entry.getValue().publishDate));
		}
		return returnList;
	}

	public Date getLastRefreshed(CompositeKey<String, String> ckey) {
		if(lastRefreshed.containsKey(ckey)){
			return lastRefreshed.get(ckey);
		}
		return null;
	}

	public void remove(CompositeKey<String, String> ckey, Value value) {
		if(dataMap.containsKey(ckey)){
			Map<String, StoredValueInfo> storedValues = dataMap.get(ckey);
			if(storedValues.containsKey(value)){
				dataMap.remove(value.getValue());
			}
			if(storedValues.isEmpty()){
				dataMap.remove(ckey);
			}
		}
	}
}
