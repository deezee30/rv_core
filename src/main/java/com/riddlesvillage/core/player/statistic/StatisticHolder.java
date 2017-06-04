/*
 * RiddlesCore
 */

package com.riddlesvillage.core.player.statistic;

import com.google.common.collect.ImmutableList;
import com.riddlesvillage.core.player.profile.AbstractCoreProfile;
import com.riddlesvillage.core.internal.command.StatsCommand;

/**
 * Used for online and offline users that can be recorded
 * in the form of statistics.
 */
@FunctionalInterface
public interface StatisticHolder {


	/**
	 * The lines used for presenting the statistical values for
	 * this player instance.
	 *
	 * Used by {@link AbstractCoreProfile}
	 * and its subclasses to set the values.
	 *
	 * @return  The custom statistic lines stored in an unmodifiable {@code List}
	 * @see     AbstractCoreProfile#getStatisticValues()
	 * @see     StatsCommand
	 */
	ImmutableList<String> getStatisticValues();
}