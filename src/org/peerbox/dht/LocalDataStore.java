package org.peerbox.dht;

import java.util.Calendar;
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
		public Calendar publishDate;
		public Calendar lastRepublish;
		public Calendar expiryDate;
		public boolean original;

		StoredValueInfo(Calendar publishDate, Calendar expiryDate, boolean original) {
			this.publishDate = publishDate;
			this.original = original;
			this.expiryDate = expiryDate;
		}
	}

	protected final Map<CompositeKey<String, String>, HashMap<String, StoredValueInfo>> dataMap;
	protected final Map<CompositeKey<String, String>, Date> lastRefreshed;

	public LocalDataStore() {
		dataMap = new HashMap<CompositeKey<String, String>, HashMap<String, StoredValueInfo>>();
		lastRefreshed = new HashMap<CompositeKey<String, String>, Date>();
	}

	public void put(CompositeKey<String, String> ckey, Value value, boolean originalPublisher, long expiryInterval) {
		if (!originalPublisher && (value.getPublicationTime() < (System.currentTimeMillis() - expiryInterval))){
			return;
		}
		HashMap<String, StoredValueInfo> valueMap;
		boolean original = false;
		if (dataMap.containsKey(ckey)) {
			valueMap = dataMap.get(ckey);
			if (valueMap.containsKey(value.getValue())) {
				Calendar originaldate = valueMap.get(value.getValue()).publishDate;
				original = valueMap.get(value.getValue()).original;
				if (originaldate == null || originaldate.before(value.getPublicationTime())) {
					valueMap.remove(value.getValue());
				} else {
					return;
				}
			}
		} else {
			valueMap = new HashMap<String, StoredValueInfo>();
			dataMap.put(ckey, valueMap);
		}
		Calendar publishDate = Calendar.getInstance();
		publishDate.setTimeInMillis(value.getPublicationTime());
		if(originalPublisher || original){
			valueMap.put(value.getValue(), new StoredValueInfo(publishDate, null, true));
		}
		else{
			Calendar expiryDate = Calendar.getInstance();
			expiryDate.setTimeInMillis(value.getPublicationTime() + expiryInterval);
			valueMap.put(value.getValue(), new StoredValueInfo(publishDate, expiryDate, false));
		}
	}

	public void updateLastRefreshed(CompositeKey<String, String> ckey) {
		if (!dataMap.containsKey(ckey)) {
			return;
		}
		if (lastRefreshed.containsKey(ckey)) {
			lastRefreshed.remove(ckey);
		}
		lastRefreshed.put(ckey, new Date());
	}

	public LinkedList<Value> get(CompositeKey<String, String> ckey) {
		if (!dataMap.containsKey(ckey)) {
			return null;
		}
		LinkedList<Value> returnList = new LinkedList<Value>();
		HashMap<String, StoredValueInfo> valueMap = dataMap.get(ckey);
		Iterator<Entry<String, StoredValueInfo>> it = valueMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, StoredValueInfo> entry = (Entry<String, StoredValueInfo>) it.next();
			Calendar now = Calendar.getInstance();
			now.setTimeInMillis(System.currentTimeMillis());
			if (entry.getValue().original || (entry.getValue().expiryDate.after(now))) {
				returnList.add(new Value(entry.getKey(), entry.getValue().publishDate.getTimeInMillis()));
			} else if(!entry.getValue().original){
//				System.out.println("removing "+ entry.getKey());
				it.remove();
			}
		}
		if (valueMap.isEmpty()) {
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
