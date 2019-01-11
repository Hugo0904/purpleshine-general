package com.falseapple.general.core;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public final class ThreadPool {
    private final static ThreadPoolExecutor pool;
    
    static {
        pool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 30, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(true), new ThreadFactoryBuilder().setDaemon(true).build());
        pool.allowCoreThreadTimeOut(true);
    }
  
    static public void execute(Runnable r) {
        corePool().execute(r);
    }
    
    static public Future<?> submit(Runnable r) {
        return corePool().submit(r);
    }
    
    static public <T> Future<T> submit(Callable<T> r) {
        return corePool().submit(r);
    }
    
    static public void close() {
        corePool().shutdownNow();   
    }

    static public ThreadPoolExecutor corePool() {
        return pool;
    }
    
    private ThreadPool() {
        //
    }
}
