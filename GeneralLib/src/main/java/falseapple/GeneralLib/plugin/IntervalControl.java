package falseapple.GeneralLib.plugin;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class IntervalControl {
	
	static private IntervalControl instance;
	
	static public IntervalControl getInstance() {
		if (instance == null) {
			synchronized (IntervalControl.class) {
				if (instance == null) {
					instance =  new IntervalControl();
				}
			}
		}
		return instance;
	}
	
	private final ConcurrentMap<Object, Instant> collector = new ConcurrentHashMap<>();
	private final Object lock = new Object();
	
	/**
	 * 重置間隔時間為當前
	 * @param key
	 * @return
	 */
	public Instant resetInterval(final Object key) {
		return collector.put(key, Instant.now());
	}
	
	/**
	 * 清除間格時間
	 * @param key
	 * @return
	 */
	public Instant clearInterval(final Object key) {
		return collector.put(key, Instant.ofEpochMilli(0));
	}
	
	/**
	 * 取得間隔時間
	 * @param key
	 * @return
	 */
	public Duration getInterval(final Object key) {
		final Instant old = collector.get(key);
		if (Objects.isNull(old)) {
			return null; // Duration.of(0, ChronoUnit.MILLIS);
		}
		return Duration.between(old, Instant.now());
	}
	
	/**
	 * 檢查時間是否滿足間格
	 * @param key
	 * @param millis
	 * @return
	 */
	public boolean iMeetInterval(final Object key, final long millis) {
		final Instant old = collector.get(key);
		if (Objects.isNull(old)) {
			return true;
		}
		return Duration.between(old, Instant.now()).toMillis() >= millis;
	}
	
	/**
	 * 檢查是否滿足間隔
	 * 如果已滿足則重置
	 * @param key
	 * @param millis
	 * @return
	 */
	public boolean safeCheckMeetAndResetInterval(final Object key, final long millis) {
		synchronized (lock) {
			return checkMeetAndResetInterval(key, millis);
		}
	}
	
	/**
	 * 檢查是否滿足間隔
	 * 如果已滿足則重置
	 * @param key
	 * @param millis
	 * @return
	 */
	public boolean checkMeetAndResetInterval(final Object key, final long millis) {
		final boolean isMeet = iMeetInterval(key, millis);
		if (isMeet) {
			resetInterval(key);
		}
		return isMeet;
	}
}
