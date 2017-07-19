package com.riddlesvillage.core.jnbt.type;

import com.riddlesvillage.core.jnbt.Tag;

/**
 * The {@code TAG_Int} tag.
 */
public final class IntTag extends Tag {

    private final int value;

    /**
     * Creates the tag with an empty name.
     *
     * @param value the value of the tag
     */
    public IntTag(int value) {
        super();
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "TAG_Int(" + value + ")";
    }
}