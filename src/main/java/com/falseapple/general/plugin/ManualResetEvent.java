package com.falseapple.general.plugin;

public final class ManualResetEvent   
{  
    private final Object monitor = new Object();  
    private volatile boolean open = false;  
      
    public ManualResetEvent(boolean initialState)  
    {  
        open = initialState;  
    }  
  
    public boolean waitOne() throws InterruptedException  
    {  
        synchronized (monitor)   
        {  
        	while (!open) monitor.wait();  
            return open;  
        }  
    }  
  
    public boolean waitOne (long timeout) throws InterruptedException  
    {  
        synchronized (monitor)  
        {  
            if (!open) monitor.wait(timeout);  
            return open;  
        }  
    }   
      
    public void set()  
    {  
        synchronized (monitor)  
        {  
            open = true;  
            monitor.notifyAll();  
        }  
    }  
  
    public void reset()  
    {  
        open = false;  
    }  
}  
