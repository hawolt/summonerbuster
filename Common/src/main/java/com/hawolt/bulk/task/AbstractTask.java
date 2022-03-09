package com.hawolt.bulk.task;

/**
 * Created: 07/03/2022 18:31
 * Author: Twitter @hawolt
 **/

public abstract class AbstractTask implements Runnable {
    protected final TaskCallback callback;

    public AbstractTask(TaskCallback callback) {
        this.callback = callback;
    }

    public abstract String getLock();
}
