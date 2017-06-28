/*
 * MaulssLib
 * 
 * Created on 10 February 2015 at 7:13 PM.
 */

package com.riddlesvillage.core.world;

import com.riddlesvillage.core.world.region.type.CuboidRegion;

/**
 * Used for looping through a set of three-dimensional points
 * in grid form, usually used in (multi-) dimensional arrays
 * or {@link com.riddlesvillage.core.world.region.Region}s containing
 * {@link Vector3D}s.
 */
@FunctionalInterface
public interface FlooredVectorLoop {

    /**
     * Loops through a set of three-dimensional (x, y, z) {@link
     * Vector3D}s stored inside
     * (multi-) dimensional arrays or {@link
     * com.riddlesvillage.core.world.region.Region}s and provides an
     * ability to use each point or {@code Vector3D} that was found
     * in the set of {@code Vector}s.
     *
     * <p>  For example, the {@link
     * CuboidRegion} is a sub class of
     * {@code Region} which holds a fixed solid three-dimensional set
     * of {@code Vector3D}s.  This method returns each of those {@code
     * Vector3D}s if used correctly.</p>
     *
     * <p>  This method was specifically built for accessing stored
     * points inside a region, such as {@link
     * com.riddlesvillage.core.world.region.Region#loop(FlooredVectorLoop)}
     * which would work as follows (assuming {@code regionObnect} is
     * an instance of {@link com.riddlesvillage.core.world.region.Region}):
     * {@code
     * final List{@literal <}Vector3D{@literal >} points = new ArrayList<>(regionObject.getVolume());
     * regionObject.loop(new FlooredVectorLoop() {
     *
     *     {@literal @}Override
     *     public void loop(Vector3D vector) {
     *         points.add(vector);
     *     }
     * });
     * }
     * Or if using {@code Java 1.8}, lamda expressions can simplify this
     * even more: {@code regionObject.loop(points :: add);}</p>
     *
     * @param	vector
     *			The individual point inside a region
     * @see		com.riddlesvillage.core.world.region.Region#loop(FlooredVectorLoop)
     */
    void loop(Vector3D vector);
}