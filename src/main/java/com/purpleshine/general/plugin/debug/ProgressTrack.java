package com.purpleshine.general.plugin.debug;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yueh
 * Debug用
 * 檢查流程是否正確, 以及上個追蹤與該次追蹤相差多少
 * (非線程安全, 建議單執行序)
 */
public final class ProgressTrack {
	
	static public final ProgressTrack Default = new ProgressTrack();
	
	private ProgressTrack() {
		// elided  
	}
	
	private final AtomicInteger step = new AtomicInteger(0);
	private long lastTrack = 0L;
	
	/**
	 * 追蹤開始
	 */
	public void Start() {
		lastTrack = System.currentTimeMillis();
		step.set(0);
	}
	
	/**
	 * 追蹤輸出
	 */
	public void Track() {
		System.out.println(String.format("[%d] step: %d, take %d ms.", Thread.currentThread().getId(), step.incrementAndGet(), System.currentTimeMillis() - lastTrack));
		lastTrack = System.currentTimeMillis();
	}
}
