package com.falseapple.general.plugin;


/**
 * @author JS-Y
 * 計數器
 */
public final class NumberCounter {
	
    private final Object monitor = new Object();  
	private long maxValue = 0;
	private long minValue = 0;
	private long count = 0L;
	
	/**
	 * 初始化建構子
	 * @param maxValue - 指定此計數器最大數為多少
	 * @param minValue - 指定此計數器最小數為多少
	 */
	public NumberCounter (long maxValue, long minValue) {
		if (maxValue <= minValue) throw new IllegalArgumentException("MaxValue must bigger than minValue.");
		this.maxValue = maxValue;
		this.minValue = minValue;
	}
	
	/**
	 * 計數減少
	 */
	synchronized public void CountDown() {
		try {
			if (count > minValue)
				count--;
		} catch (Exception e) {
			count = 0L;
		}
	}
	
	/**
	 * 計數增加
	 */
	synchronized public void CountUp() {
		try {
			if (count < maxValue)
				count++;
		} catch (Exception e) {
			count = 0L;
		}
	}
	
	/**
	 * 計數減少並且通知wait中的執行緒
	 */
	synchronized public void CountDownAndNotify() {
		if (count > minValue)
			count--;
		if (count < maxValue)
			notifyAll();
	}

	/**
	 * 設置計數為指定數並且通知wait中的執行緒
	 */
	synchronized public void SetCountAndNotify(long value) {
		count = value;
		if (count < minValue)
			count = minValue;
		if (count > maxValue)
			count = maxValue;
		notifyAll();
	}
	
	/**
	 * 判斷計數器是否已到最大值, 是則會進入wait
	 * @throws InterruptedException 
	 */
	synchronized public void maxToWait() throws InterruptedException {
		while (count >= maxValue) monitor.wait();
	}
	
	/**
	 * 判斷計數器是否已到最大值, 是則會進入wait
	 * @param timeout - time out 時間
	 */
	public void maxToWait(long timeout) {
		if (count >= maxValue) {
			synchronized (this) {
				if (count >= maxValue) {
					try {
						wait(timeout);
					} catch (InterruptedException e) {
						//
					}
				}
			}
		}
	}
	
	/**
	 * 增加指定數至計數器
	 * @param size - 指定數
	 */
	synchronized public void increaseSpecialCount(long size) {
		try {
			count += size;
		} catch (Exception e) {
			count = Long.MAX_VALUE;
		}
	}
	
	/**
	 * 改變最大數
	 * @param maxValue - 新的最大數
	 */
	synchronized public void setMaxValue(long maxValue) {
		if (maxValue > minValue)
			this.maxValue = maxValue;
	}
	
	/**
	 * 改變最小數
	 * @param maxValue - 新的最小數
	 */
	synchronized public void setMinValue(long minValue) {
		if (minValue < maxValue)
			this.minValue = maxValue;
	}
	
	/**
	 * 取得目前計數器的值
	 * @return - 當前值
	 */
	synchronized public long getCount() {
		return this.count;
	}
	
	/**
	 * 將計數器的值歸0
	 */
	synchronized public void clearCount() {
		this.count = 0L;
	}
	
	/**
	 * 將計數器的值歸0 和通知wait中的執行緒
	 */
	synchronized public void clearCountAndNotify() {
		this.count = 0L;
		notifyAll();
	}
	
	/**
	 * 將計數器的值, 直接設為最大值
	 */
	synchronized public void setMaxCount() {
		this.count = maxValue;
	}
	
	/**
	 * 將計數器的值, 直接設為最小值
	 */
	synchronized public void setMinCount() {
		this.count = minValue;
	}

	/**
	 * 取得計數器的最大值
	 */
	synchronized public long getMaxValue() {
		return maxValue;
	}

	/**
	 * 取得計數器的最小值
	 */
	synchronized public long getMinValue() {
		return minValue;
	}
	
	/**
	 * 隨機喚醒一個wait中的執行緒
	 */
	synchronized public void countNotify() {
		monitor.notify();
	}
	
	/**
	 * 喚醒所有wait中的執行緒
	 */
	synchronized public void countNotifyAll() {
		monitor.notifyAll();
	}
	
	/**
	 * 判斷當前值是否已達到最大值
	 * @return - true if is max.
	 */
	synchronized public boolean isCountMax() {
		return count >= maxValue;
	}
	
	/**
	 * 重製整個計數器
	 */
	synchronized public void resetAll() {
		maxValue = 0;
		minValue = 0;
		count = 0L;
	}
}
