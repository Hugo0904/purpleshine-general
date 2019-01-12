package com.purpleshine.general.plugin;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class Scheduler {
	
	protected final ConcurrentHashMap<Object, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();
	
	private final ScheduledExecutorService executorService;
	private final ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).build();
	
	/**
	 * 建立一個單線程實例
	 */
	public Scheduler() {
		executorService = Executors.newSingleThreadScheduledExecutor(threadFactory);
	}
	
	/**
	 * 建立一個多線程實例
	 * @param coreSize 核心數量
	 */
	public Scheduler(int coreSize) {
		executorService = Executors.newScheduledThreadPool(Math.max(coreSize, 1), threadFactory);
	}
	
	/**
	 * 透過scheduleName停止future
	 * @param scheduleKey
	 * @param rightNow
	 */
	protected void cancelScheduleByName(Object scheduleKey, boolean rightNow) {
		if (futures.containsKey(scheduleKey)) {
			futures.remove(scheduleKey).cancel(rightNow);
		}
	}
	
	/**
	 * 啟動排程
	 * 並能依scheduleKey儲存Future
	 * @param scheduleKey
	 * @param runnable
	 * @param initialDelay
	 * @param delay
	 * @param unit
	 * @return
	 */
	protected boolean executetFixedSchedule(Object scheduleKey, Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
		return Objects.isNull(futures.putIfAbsent(scheduleKey, executetFixedSchedule(runnable, initialDelay, delay, unit)));
	}
	
	/**
	 * 啟動排程
	 * 並能依scheduleKey儲存Future
	 * @param scheduleKey
	 * @param runnable
	 * @param initialDelay
	 * @param delay
	 * @param unit
	 * @return
	 */
	protected boolean executetWithFixedSchedule(Object scheduleKey, Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
		return Objects.isNull(futures.putIfAbsent(scheduleKey, executetWithFixedSchedule(runnable, initialDelay, delay, unit)));
	}
	
	/**
	 * 啟動排程
	 * @param runnable
	 * @param initialDelay
	 * @param delay
	 * @param unit
	 * @return
	 */
	protected ScheduledFuture<?> executetFixedSchedule(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
		return executorService.scheduleAtFixedRate(runnable, initialDelay, delay, unit);
	}
	
	/**
	 * 啟動排程
	 * @param runnable
	 * @param initialDelay
	 * @param delay
	 * @param unit
	 * @return
	 */
	protected ScheduledFuture<?> executetWithFixedSchedule(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
		return executorService.scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
	}
}
