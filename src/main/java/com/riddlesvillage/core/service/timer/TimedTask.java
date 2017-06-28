package com.riddlesvillage.core.service.timer;

import org.apache.commons.lang3.Validate;

public abstract class TimedTask<T> {

    private final Timer timer = new Timer();
    private T t;

    public final T execute() throws Exception {
        return executeAndThen(() -> {});
    }

    public final T executeAndThen(final Runnable runnable) throws Exception {
        Validate.notNull(runnable);
        timer.onFinishExecute(runnable).start();
        t = process();
        timer.forceStop();
        return t;
    }

    public T getT() {
        return t;
    }

    public final Timer getTimer() {
        return timer;
    }

    protected abstract T process() throws Exception;
}