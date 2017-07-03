/*
 * rv_core
 * 
 * Created on 01 July 2017 at 3:37 PM.
 */

package com.riddlesvillage.core.pgm.rating;

import java.util.UUID;

public interface RatableProfile {

    String getName();

    UUID getUuid();

    int getRating();

    void setRating(final int newRating);

    double getRatingDeviation();

    void setRatingDeviation(final double newRatingDeviation);

    double getCFactor();

    default int getMinConfidentRating() {
        return getRating() - (int) (1.96d * getRatingDeviation());
    }

    default int getMaxConfidentRating() {
        return getRating() + (int) (1.96d * getRatingDeviation());
    }
}