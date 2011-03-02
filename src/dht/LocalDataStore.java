package dht;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LocalDataStore {
	protected final Map<String, List<MapDataFilter<String, String>>> dataFilters;
	protected final Map<CompositeKey<String, String>, String> dataMap;
	
	public LocalDataStore() {
		dataFilters = new HashMap<String, List<MapDataFilter<String, String>>>();
		dataMap = new HashMap<CompositeKey<String, String>, String>();
	}
	
	public void registerDataFilter(String primaryKey, MapDataFilter<String, String> dataFilter) {
		List<MapDataFilter<String, String>> dataFilterList = dataFilters.get(primaryKey);
		if (dataFilterList == null) {
			dataFilterList = new LinkedList<MapDataFilter<String, String>>();
			dataFilters.put(primaryKey, dataFilterList);
		}
		dataFilterList.add(dataFilter);
	}
	
	public boolean put(CompositeKey<String, String> ckey, String value) {
		List<MapDataFilter<String, String>> dataFilterList = dataFilters.get(ckey.getPrimaryKey());
		if (dataFilterList != null) {
			for (MapDataFilter<String, String> dataFilter : dataFilterList) {
				if (!dataFilter.isValid(ckey.getSecondaryKey(), value)) {
					return false;
				}
			}
		}
		dataMap.put(ckey, value);
		return true;
	}
	
	public String get(CompositeKey<String, String> ckey) {
		return dataMap.get(ckey);
	}
}
