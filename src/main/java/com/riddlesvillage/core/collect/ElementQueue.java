/*
 * MaulssLib
 * 
 * Created on 25 December 2014 at 6:27 PM.
 */

package com.riddlesvillage.core.collect;

import java.util.Collection;
import java.util.Collections;

public class ElementQueue<E> extends EnhancedList<E> {

	private static final long serialVersionUID = 5216613009142713027L;

	private RotateTask<E> onRotate = element -> {};

	public ElementQueue() {}

	public ElementQueue(int initialCapacity) {
		super(initialCapacity);
	}

	@SafeVarargs
	public ElementQueue(E... elements) {
		super(elements);
	}

	public ElementQueue(Collection<? extends E> c) {
		super(c);
	}

	public void rotate() {
		rotate(1);
	}

	public void rotate(int distance) {
		if (distance >= size()) return;

		E first = get(0);
		Collections.rotate(this, distance);
		onRotate.onRotate(first);
	}

	public final void onRotate(RotateTask<E> onRotate) {
		this.onRotate = onRotate;
	}
}