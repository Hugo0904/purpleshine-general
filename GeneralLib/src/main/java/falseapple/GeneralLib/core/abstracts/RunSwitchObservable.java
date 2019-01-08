package falseapple.GeneralLib.core.abstracts;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import falseapple.GeneralLib.core.interfaces.Switchable;
import falseapple.GeneralLib.plugin.PublishScheduler;

/**
 * @author yueh
 * 擁有啟動或停止的單一自動排程
 * @param <SDate>
 */
public abstract class RunSwitchObservable<SDate> extends PublishScheduler<SDate> implements Runnable, Switchable {
    
    private final AtomicBoolean execute = new AtomicBoolean();
    private volatile ScheduledFuture<?> scheduledFuture;
    private volatile int period;
    
    public RunSwitchObservable(int period) {
        this.period = period;
    }
    
    /**
     * 啟動資料發送中心
     */
    @Override
    public boolean start(Object... options) {
        if (this.execute.compareAndSet(false, true)) {
            scheduledFuture = this.executetFixedSchedule(this, 1, period, TimeUnit.MILLISECONDS);
        }
        return isRunning();
    }
    
    @Override
    public boolean stop() {
        if (this.execute.compareAndSet(true, false)) {
            this.scheduledFuture.cancel(true);
            this.scheduledFuture = null;
        }
        return !isRunning();
    }
    
    @Override
    public boolean isRunning() {
        return this.execute.get() && !this.scheduledFuture.isCancelled() && !this.scheduledFuture.isDone();
    }
    
    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}