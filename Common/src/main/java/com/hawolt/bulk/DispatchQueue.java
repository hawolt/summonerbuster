package com.hawolt.bulk;

import com.hawolt.Logger;
import com.hawolt.bulk.task.AbstractTask;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created: 04/03/2022 16:47
 * Author: Twitter @hawolt
 **/

public class DispatchQueue {

    public static long DISPATCH_DELAY = 5000L;

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    private static final ExecutorService POOL = Executors.newCachedThreadPool();
    private static final List<Runnable> LIST = new LinkedList<>();
    private static final Set<String> UNIQUE_SET = new HashSet<>();

    private DispatchQueue() {
        SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            try {
                if (!LIST.isEmpty()) {
                    POOL.execute(LIST.remove(0));
                }
            } catch (Exception e) {
                Logger.error(e);
            }
        }, DISPATCH_DELAY, DISPATCH_DELAY, TimeUnit.MILLISECONDS);
    }

    static {
        new DispatchQueue();
    }

    public static void submit(AbstractTask task) {
        submit(task, false);
    }

    public static void submit(AbstractTask task, boolean priority) {
        String lock = task.getLock();
        if (UNIQUE_SET.contains(lock)) return;
        else UNIQUE_SET.add(lock);
        if (!priority) LIST.add(task);
        else LIST.add(0, task);
    }

    public static void unlock(String code) {
        UNIQUE_SET.remove(code);
    }
}
