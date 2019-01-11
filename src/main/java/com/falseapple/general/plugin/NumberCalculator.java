package com.falseapple.general.plugin;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author JS-Y
 * 計數器 - 用來取得平均值、最大值、最小值、最後一個值
 */
public final class NumberCalculator {
	
	private ReentrantLock lock = new ReentrantLock(true);
	private volatile long minValue = 0L;
	private volatile long maxValue = 0L;
	private volatile double avgValue = 0L;
	private volatile long total = 0L;
	private volatile long lastValue = 0L;
	private volatile long lastUpdate = 0L;
	
	/**
	 * 重製計數器
	 */
	public void reset() {
		minValue = 0L;
		maxValue = 0L;
		avgValue = 0L;
		total = 0L;
		lastValue = 0L;
		lastUpdate = 0L;
	}
	
	/**
	 * 將指定值新增至計數器
	 * @param value - 值
	 */
	synchronized public void setValue(long value) {
		lock.lock();
		try {
			for (boolean success = false; !success;) {
				try {
					if (value < minValue) {
						minValue = value;
					}
					
					if (value > maxValue) {
						maxValue = value;
					}
					
					lastValue = value;
					avgValue = ((avgValue * total) + value) / ++total;
					success = true;
				} catch (Exception e) {
					reset();
				}
			}
			lastUpdate = System.currentTimeMillis();
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * 取得當前計數過的最小值
	 * @return - 值
	 */
	public long getMinValue() {
		return minValue;
	}
	
	/**
	 * 取得當前計數過的最大值
	 * @return - 值
	 */
	public long getMaxValue() {
		return maxValue;
	}
	
	/**
	 * 取得當前計數過的平均值
	 * @return - 值
	 */
	public double getAvgValue() {
		return avgValue;
	}
	
	/**
	 * 取得已經計數的次數
	 * @return - 值
	 */
	public long getTotal() {
		return total;
	}
	
	/**
	 * 取得最後一次計數的值
	 * @return - 值
	 */
	public long getLastValue() {
		return lastValue;
	}
	
	/**
	 * 取得最後計數的時間
	 * @return - 時間
	 */
	public long getLastUpdate() {
		return lastUpdate;
	}
	
}
