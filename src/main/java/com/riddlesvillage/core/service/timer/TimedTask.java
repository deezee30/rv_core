package com.riddlesvillage.core.service.timer;

import org.apache.commons.lang3.Validate;

public abstract class TimedTask<T> {

	private final Timer timer = new Timer();
	private T t;

	public final T execute() throws Exception {
		return executeAndThen(() -> {});
	}

	public final T executeAndThen(Runnable runnable) throws Exception {
		timer.onFinishExecute(Validate.notNull(runnable)).start();
		t = process();
		timer.forceStop();
		return t;
	}


	public final Timer getTimer() {
		return timer;
	}

	public final T getT() {
		return t;
	}

	protected abstract T process() throws Exception;
}