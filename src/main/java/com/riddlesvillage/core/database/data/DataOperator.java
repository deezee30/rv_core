/*
 * rv_core
 * 
 * Created on 04 June 2017 at 4:48 PM.
 */

package com.riddlesvillage.core.database.data;

public enum DataOperator {

	//Increase whats current in database.
	$INC(0, "$inc"),

	//Multiply by current in database.
	$MUL(1, "$mul"),

	//Push into an arraylist
	$PUSH(2, "$push"),

	//Set a field.
	$SET(3, "$set"),

	//Remove from an ArrayList
	$PULL(4, "$pull"),

	//remove from a document
	$UNSET(5, "$unset"),

	//remove from a document
	$POP(6, "$pop");

	private int id;
	private String operator;

	DataOperator(int id, String operator) {
		this.id = id;
		this.operator = operator;
	}

	public int getId() {
		return id;
	}

	public String getOperator() {
		return operator;
	}

	@Override
	public String toString() {
		return operator;
	}
}
