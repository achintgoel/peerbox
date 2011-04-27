package org.peerbox.dht;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.peerbox.kademlia.Value;

/**
 * Dump data storage TODO: Replace with standard map. NOTE: This does NOT do any
 * filtering.
 * 
 * @author rajiv
 * 
 */
public class LocalDataStore {

	protected class StoredValueInfo {
		public Date publishDate;
		public Date lastRepublish;
		public boolean original;

		StoredValueInfo(Date publishDate, boolean original) {
			this.publishDate = publishDate;
			this.original = original;
		}
	}

	protected final Map<CompositeKey<String, String>, HashMap<String, StoredValueInfo>> dataMap;
	protected final Map<CompositeKey<String, String>, Date> lastRefreshed;
	public int expiryInterval;

	public LocalDataStore(int expiryInterval) {
		dataMap = new HashMap<CompositeKey<String, String>, HashMap<String, StoredValueInfo>>();
		lastRefreshed = new HashMap<CompositeKey<String, String>, Date>();
		this.expiryInterval = expiryInterval;
	}

	public void put(CompositeKey<String, String> ckey, Value value, boolean originalPublisher) {
		if (value.getTimestamp().before(new Date(System.currentTimeMillis() - (expiryInterval * 1000)))) {
			return;
		}
		HashMap<String, StoredValueInfo> valueMap;
		if (dataMap.containsKey(ckey)) {
			valueMap = dataMap.get(ckey);
			if (valueMap.containsKey(value.getValue())) {
				Date originaldate = valueMap.get(value.getValue()).publishDate;
				if (originaldate.before(value.getTimestamp())) {
					valueMap.remove(value.getValue());
				} else {
					return;
				}
			}
		} else {
			valueMap = new HashMap<String, StoredValueInfo>();
			dataMap.put(ckey, valueMap);
		}
		valueMap.put(value.getValue(), new StoredValueInfo(value.getTimestamp(), originalPublisher));
	}

	public void updateLastRefreshed(CompositeKey<String, String> ckey) {
		if (!dataMap.containsKey(ckey)) {
			return;
		}
		if (lastRefreshed.containsKey(ckey)) {
			lastRefreshed.remove(ckey);
		}
		lastRefreshed.put(ckey, new Date(System.currentTimeMillis()));
	}

	public LinkedList<Value> get(CompositeKey<String, String> ckey) {
		if (!dataMap.containsKey(ckey)) {
			return null;
		}
		LinkedList<Value> returnList = new LinkedList<Value>();
		HashMap<String, StoredValueInfo> valueMap = dataMap.get(ckey);
		Iterator<Entry<String, StoredValueInfo>> it = valueMap.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, StoredValueInfo> entry = (Entry<String, StoredValueInfo>) it.next();
			if (entry.getValue().publishDate.after(new Date(System.currentTimeMillis() - (expiryInterval * 1000)))){
				returnList.add(new Value(entry.getKey(), entry.getValue().publishDate));
			}
			else{
				it.remove();
			}
		}
		if(valueMap.isEmpty()){
			dataMap.remove(ckey);
			return null;
		}
		return returnList;
	}

	public Date getLastRefreshed(CompositeKey<String, String> ckey) {
		if (lastRefreshed.containsKey(ckey)) {
			return lastRefreshed.get(ckey);
		}
		return null;
	}

	public void remove(CompositeKey<String, String> ckey, Value value) {
		if (dataMap.containsKey(ckey)) {
			Map<String, StoredValueInfo> storedValues = dataMap.get(ckey);
			if (storedValues.containsKey(value)) {
				dataMap.remove(value.getValue());
			}
			if (storedValues.isEmpty()) {
				dataMap.remove(ckey);
			}
		}
	}
}
