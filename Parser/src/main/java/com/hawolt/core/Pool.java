package com.hawolt.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * Created: 03/03/2022 18:39
 * Author: Twitter @hawolt
 **/
public class Pool extends ThreadPoolExecutor {

    private Pool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    private Pool(int initialCoreSize) {
        this(initialCoreSize, initialCoreSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }

    public static Pool spawn(int initialCoreSize) {
        return new Pool(initialCoreSize);
    }
}
