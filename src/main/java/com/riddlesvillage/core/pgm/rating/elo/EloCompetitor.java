/*
 * rv_core
 * 
 * Created on 02 July 2017 at 11:50 PM.
 */

package com.riddlesvillage.core.pgm.rating.elo;

import com.riddlesvillage.core.pgm.rating.Competitor;
import com.riddlesvillage.core.pgm.rating.KFactor;
import com.riddlesvillage.core.pgm.rating.StaticKFactor;
import com.riddlesvillage.core.util.MathUtil;

public interface EloCompetitor extends Competitor {

    int INITIAL_RATING = 1200;

    default double getRelativeEloRating() {
        return MathUtil.round((double) getRating() / (double) INITIAL_RATING, 2);
    }

    default KFactor getKFactor() {
        return StaticKFactor.getKFactor(getRating(), isProvisional());
    }
}