/*
 * rv_core
 * 
 * Created on 13 June 2017 at 1:47 PM.
 */

package com.riddlesvillage.core.scoreboard.handler;

public interface IScoreboardHandler extends Cloneable {

	IScoreboardHandler refresh();

	void destroy();
}