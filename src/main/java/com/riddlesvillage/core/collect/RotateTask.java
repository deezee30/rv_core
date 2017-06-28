/*
 * EnhancedLib
 * 
 * Created on 16 June 2015 at 2:21 AM.
 */

package com.riddlesvillage.core.collect;

/**
 * The interface Rotate task.
 *
 * <p>Goes together with {@link ElementQueue<E>}</p>
 *
 * @param <E> the type parameter
 */
@FunctionalInterface
public interface RotateTask<E> {

    /**
     * Gets called whenever an {@link ElementQueue<E>}
     * rotates.
     *
     * @param element the element
     */
    void onRotate(final E element);
}