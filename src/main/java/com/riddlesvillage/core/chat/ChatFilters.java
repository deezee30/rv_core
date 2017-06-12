/*
 * rv_core
 * 
 * Created on 11 June 2017 at 8:59 PM.
 */

package com.riddlesvillage.core.chat;

import com.riddlesvillage.core.collect.EnhancedList;

import java.util.Iterator;

public final class ChatFilters implements Iterable<ChatBlockFilter> {

	private static final ChatFilters INSTANCE = new ChatFilters();
	private EnhancedList<ChatBlockFilter> filters = new EnhancedList<>();

	{
		addFilter(new MuteFilter());
		addFilter(new AdvertisementFilter());
		addFilter(new SpamFilter());
	}

	public void addFilter(ChatBlockFilter filter) {
		filters.addIf(filter != null, filter);
	}

	@Override
	public Iterator<ChatBlockFilter> iterator() {
		return filters.iterator();
	}

	public static ChatFilters getInstance() {
		return INSTANCE;
	}
}