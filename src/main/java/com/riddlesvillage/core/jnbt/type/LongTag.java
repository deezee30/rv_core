package com.riddlesvillage.core.jnbt.type;

import com.riddlesvillage.core.jnbt.Tag;

/**
 * The {@code TAG_Long} tag.
 * 
 */
public final class LongTag extends Tag {

    private final long value;

    /**
     * Creates the tag with an empty name.
     *
     * @param value the value of the tag
     */
    public LongTag(long value) {
        super();
        this.value = value;
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "TAG_Long(" + value + ")";
    }
}