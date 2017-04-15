package com.riddlesvillage.core.database.data;

public enum EnumOperators {

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
    private String UO;

    EnumOperators(int id, String UO) {
        this.id = id;
        this.UO = UO;
    }

    public int getId() {
        return id;
    }

    public String getUO() {
        return UO;
    }
}
