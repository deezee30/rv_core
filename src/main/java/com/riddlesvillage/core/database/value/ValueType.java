/*
 * MySQLLib
 *
 * Created on 22 July 2014 at 2:47 AM.
 */

package com.riddlesvillage.core.database.value;

public enum ValueType {

	/** Sets the exact value in the database. */
	SET,

	/** Adds a specific amount to the column in the database. */
	GIVE,

	/** Takes a specific amount from the column in the database. */
	TAKE
}