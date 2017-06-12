/*
 * rv_core
 * 
 * Created on 12 June 2017 at 4:55 PM.
 */

package com.riddlesvillage.core;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public abstract class Violation<Target> extends TimerTask {

	private final Target target;
	private final int toleration;
	private Timer timer;
	private volatile int violations = 0;

	protected Violation(Target target, int toleration) {
		this.target = target;
		this.toleration = toleration;
	}

	@Override
	public final void run() {
		if (violations != 0) {
			violations--;
			onViolationDecrease(target);
		}
	}

	public abstract void onMaxViolations(Target target);

	public abstract void onViolation(Target target);

	public void onViolationDecrease(Target target) {}

	public final void enableCooldown(int time, TimeUnit units) {
		if (timer != null) timer.cancel();

		long millis = units.toMillis(time);
		timer = new Timer();
		timer.scheduleAtFixedRate(this, millis, millis);
	}

	public final boolean addViolation() {
		boolean tolerated = ++violations != toleration;

		if (tolerated) {
			onViolation(target);
		} else {
			onMaxViolations(target);
			clear();
		}

		return tolerated;
	}

	public final int getViolations() {
		return violations;
	}

	public final int getRemainingViolations() {
		return toleration - violations;
	}

	public final void clear() {
		violations = 0;
	}

	public Target getTarget() {
		return target;
	}

	public int getToleration() {
		return toleration;
	}
}