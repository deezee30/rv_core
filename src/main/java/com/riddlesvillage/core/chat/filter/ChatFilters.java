/*
 * rv_core
 * 
 * Created on 11 June 2017 at 8:59 PM.
 */

package com.riddlesvillage.core.chat.filter;

import com.google.common.collect.ImmutableList;
import com.riddlesvillage.core.Core;
import com.riddlesvillage.core.collect.EnhancedList;
import org.apache.commons.lang.Validate;

import java.util.Iterator;

/**
 * The type Chat filters.
 */
public final class ChatFilters implements Iterable<ChatBlockFilter> {

    private static final ChatFilters INSTANCE = new ChatFilters();
    private EnhancedList<ChatBlockFilter> filters = new EnhancedList<>();

    /**
     * Registers defaults.
     */
    public void registerDefaults() {
        addFilter(new MuteFilter());
        addFilter(new AdvertisementFilter());
        addFilter(new SpamFilter());
        addFilter(new SingleCharacterFilter());
    }

    /**
     * Add filter.
     *
     * @param filter the filter
     */
    public void addFilter(final ChatBlockFilter filter) {
        Validate.notNull(filter);

        Core.debug("Adding chat filter: " + filter.getClass().getSimpleName());
        filters.add(filter);
    }

    /**
     * Gets registered filters.
     *
     * @return the filters
     */
    public ImmutableList<ChatBlockFilter> getFilters() {
        return filters.getImmutableElements();
    }

    @Override
    public Iterator<ChatBlockFilter> iterator() {
        return filters.iterator();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static ChatFilters getInstance() {
        return INSTANCE;
    }
}