package com.purpleshine.general.helpers;

public class ThreadUtil {
    static public void sleep(long millis) throws RuntimeException {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
