package org.peerbox.kademlia;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.peerbox.dht.CompositeKey;
import org.peerbox.dht.Value;
import org.peerbox.kademlia.messages.StoreResponse;

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
		public Calendar expiryDate;
		public boolean original;
		transient public TimerTask republishTask;

		StoredValueInfo(Calendar publishDate, Calendar expiryDate, boolean original, TimerTask timerTask) {
			this.publishDate = publishDate;
			this.original = original;
			this.expiryDate = expiryDate;
			republishTask = timerTask;
		}
	}

	protected final Map<CompositeKey<String, String>, HashMap<String, StoredValueInfo>> dataMap;
	protected final Map<CompositeKey<String, String>, Date> lastRefreshed;
	protected final Timer republishTimer;
	protected final NetworkInstance networkInstance;

	public LocalDataStore(NetworkInstance ni) {
		dataMap = new HashMap<CompositeKey<String, String>, HashMap<String, StoredValueInfo>>();
		lastRefreshed = new HashMap<CompositeKey<String, String>, Date>();
		republishTimer = new Timer();
		networkInstance = ni;
	}

	public void put(final Key ckey, final Value value, boolean originalPublisher, long expiryInterval) {
		if (!originalPublisher && (value.getPublicationTime() < (System.currentTimeMillis() - expiryInterval))) {
			return;
		}
		HashMap<String, StoredValueInfo> valueMap;
		boolean original = false;
		if (dataMap.containsKey(ckey)) {
			valueMap = dataMap.get(ckey);
			if (valueMap.containsKey(value.getValue())) {
				StoredValueInfo originalValueInfo = valueMap.get(value.getValue());
				Calendar originaldate = originalValueInfo.publishDate;
				original = originalValueInfo.original;
				Calendar newDate = Calendar.getInstance();
				newDate.setTimeInMillis(value.getPublicationTime());
				if (originaldate == null || originaldate.before(newDate)) {
					originalValueInfo.republishTask.cancel();
					valueMap.remove(value.getValue());
				} else {
					return;
				}
			}
		} else {
			valueMap = new HashMap<String, StoredValueInfo>();
			dataMap.put(ckey, valueMap);
		}
		TimerTask timerTask;
		Calendar publishDate = Calendar.getInstance();
		publishDate.setTimeInMillis(value.getPublicationTime());
		if (originalPublisher || original) {
			timerTask = new TimerTask() {
				@Override
				public void run() {
					networkInstance.storeValue(ckey, new Value(value.getValue()),
							new ResponseListener<StoreResponse>() {
								@Override
								public void onResponseReceived(StoreResponse response) {
									// do nothing
								}

								@Override
								public void onFailure() {
									// do nothing

								}

							});
				}
			};
			valueMap.put(value.getValue(), new StoredValueInfo(publishDate, null, true, timerTask));
		} else {
			Calendar expiryDate = Calendar.getInstance();
			expiryDate.setTimeInMillis(value.getPublicationTime() + expiryInterval);
			timerTask = new TimerTask() {
				@Override
				public void run() {
					networkInstance.storeValueNetwork(ckey, Arrays.asList(new Value(value.getValue())),
							new ResponseListener<StoreResponse>() {
								@Override
								public void onResponseReceived(StoreResponse response) {
									// do nothing
								}

								@Override
								public void onFailure() {
									// do nothing

								}

							});
				}
			};
			valueMap.put(value.getValue(), new StoredValueInfo(publishDate, expiryDate, false, timerTask));
		}
		try {
			republishTimer.schedule(timerTask, 1000 * ((originalPublisher || original) ? networkInstance
					.getConfiguration().getRepublishInterval() : networkInstance.getConfiguration()
					.getReplicateInterval()));
		} catch (IllegalStateException e) {
			// This is OK; timer was already canceled
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
			} else if (!entry.getValue().original) {
				entry.getValue().republishTask.cancel();
				// System.out.println("removing "+ entry.getKey());
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
