package com.lk.redo.commons.util.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadFactoryUtils
 * @Author: luokun
 */
public class ThreadFactoryUtils {

    public static ThreadFactory createByName(final String name) {
        final AtomicInteger idc = new AtomicInteger();
        return r -> {
            Thread t = new Thread(r, name + idc.incrementAndGet());
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        };
    }
}
