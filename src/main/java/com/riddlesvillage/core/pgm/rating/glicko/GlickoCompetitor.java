/*
 * rv_core
 * 
 * Created on 03 July 2017 at 1:11 AM.
 */

package com.riddlesvillage.core.pgm.rating.glicko;

import com.riddlesvillage.core.pgm.rating.Competitor;
import com.riddlesvillage.core.util.MathUtil;

public interface GlickoCompetitor extends Competitor {

    int INITIAL_RATING = 1500;
    double INITIAL_DEVIATION = 350d;

    double getRatingDeviation();

    void setRatingDeviation(double deviation);

    default double getRelativeGlickoRating() {
        return MathUtil.round((double) getRating() / (double) INITIAL_RATING, 2);
    }

    default int getMinConfidentRating() {
        return getRating() - (int) (1.96d * getRatingDeviation());
    }

    default int getMaxConfidentRating() {
        return getRating() + (int) (1.96d * getRatingDeviation());
    }
}