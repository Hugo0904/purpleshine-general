package falseapple.GeneralLib.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ObjectMap {
	
	private final Map<String, Object> map;
	
	public ObjectMap(boolean threadSafe) {
		map = threadSafe ? new ConcurrentHashMap<>() : new HashMap<>();
	}
	
	/**
	 * 設置Cookie
	 * @param key
	 * @param value
	 * @return
	 */
	public <T> T setValue(String key, T value) {
		map.put(key, value);
		return value;
	}
	
	/**
	 * 取得Cookie
	 * @param key
	 * @param type
	 * @return
	 */
	public <T> T getValue(String key, Class<T> type) {
		return map.containsKey(key) ? type.cast(map.get(key)) : null;
	}
}
