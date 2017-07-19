package com.riddlesvillage.core.jnbt.type;

import com.riddlesvillage.core.jnbt.Tag;

/**
 * The {@code TAG_End} tag.
 */
public final class EndTag extends Tag {

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public String toString() {
        return "TAG_End";
    }
}