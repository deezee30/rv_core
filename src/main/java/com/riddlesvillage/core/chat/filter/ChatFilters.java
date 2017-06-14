/*
 * rv_core
 * 
 * Created on 11 June 2017 at 8:59 PM.
 */

package com.riddlesvillage.core.chat.filter;

import com.google.common.collect.ImmutableList;
import com.riddlesvillage.core.Messaging;
import com.riddlesvillage.core.collect.EnhancedList;

import java.util.Iterator;

public final class ChatFilters implements Iterable<ChatBlockFilter> {

	private static final ChatFilters INSTANCE = new ChatFilters();
	private EnhancedList<ChatBlockFilter> filters = new EnhancedList<>();

	public void registerDefaults() {
		addFilter(new MuteFilter());
		addFilter(new AdvertisementFilter());
		addFilter(new SpamFilter());
		addFilter(new SingleCharacterFilter());
	}

	public void addFilter(ChatBlockFilter filter) {
		if (filter == null) return;

		Messaging.debug("Adding chat filter: " + filter.getClass().getSimpleName());
		filters.add(filter);
	}

	public ImmutableList<ChatBlockFilter> getFilters() {
		return filters.getImmutableElements();
	}

	@Override
	public Iterator<ChatBlockFilter> iterator() {
		return filters.iterator();
	}

	public static ChatFilters getInstance() {
		return INSTANCE;
	}
}