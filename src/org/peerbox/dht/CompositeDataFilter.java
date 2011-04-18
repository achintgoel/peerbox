package org.peerbox.dht;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.peerbox.kademlia.Value;

public class CompositeDataFilter implements MapDataFilter<CompositeKey<String, String>, String> {
	protected final Map<String, List<MapDataFilter<String, String>>> dataFilters;

	public CompositeDataFilter() {
		dataFilters = new HashMap<String, List<MapDataFilter<String, String>>>();
	}
	
	public void registerDataFilter(String primaryKey, MapDataFilter<String, String> dataFilter) {
		List<MapDataFilter<String, String>> dataFilterList = dataFilters.get(primaryKey);
		if (dataFilterList == null) {
			dataFilterList = new LinkedList<MapDataFilter<String, String>>();
			dataFilters.put(primaryKey, dataFilterList);
		}
		dataFilterList.add(dataFilter);
	}
	
	public boolean isValid(CompositeKey<String, String> key, String value) {
		List<MapDataFilter<String, String>> dataFilterList = dataFilters.get(key.getPrimaryKey());
		if (dataFilterList != null) {
			for (MapDataFilter<String, String> dataFilter : dataFilterList) {
				if (!dataFilter.isValid(key.getSecondaryKey(), value)) {
					return false;
				}
			}
		}
		return true;
	}
}
