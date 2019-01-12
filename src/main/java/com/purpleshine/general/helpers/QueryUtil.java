package com.purpleshine.general.helpers;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public final class QueryUtil {

	/**
	 * 將字串經過A-Z排序, 並轉換成query
	 * @param collection
	 * @return
	 */
	static public String buildSordQuery(final Map<String, ?> collection) {
		if (collection.isEmpty()) return "";
		final List<Entry<String, ?>> list = collection.entrySet().stream()
				.sorted(Comparator.comparing(e -> e.getKey()))
				.collect(Collectors.toList());
		final StringBuilder sb = new StringBuilder();
		for (Entry<String, ?> entry : list) {
			sb.append("&" + entry.getKey() + "=" + entry.getValue().toString());
		}
		return sb.substring(1);
	}
}
