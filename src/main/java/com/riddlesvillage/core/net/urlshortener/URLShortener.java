package com.riddlesvillage.core.net.urlshortener;

import com.google.common.util.concurrent.ListenableFuture;
import com.riddlesvillage.core.Messaging;
import com.riddlesvillage.core.service.ServiceExecutor;
import com.riddlesvillage.core.service.timer.TimedCallableTask;
import com.riddlesvillage.core.util.StringUtil;

import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class URLShortener extends TimedCallableTask<URL> {

	protected final String longUrl;

	private static final String[] removables = {
			// do not change order
			"ftp://" , "https://" , "http://"
	};

	protected URLShortener(String longUrl) {
		for (String keyword : removables) {
			StringUtil.remove(longUrl, keyword);
		}

		this.longUrl = longUrl;
	}

	public final URL shorten() {
		ListenableFuture<URL> future = ServiceExecutor.getCachedExecutor().submit(this);
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public final String getLongUrl() {
		return longUrl;
	}

	@Override
	public final URL call() throws Exception {
		return executeAndThen(() -> Messaging.debug(
				"URL '%s' was shortened to '%s' in %sms",
				longUrl,
				getT(),
				getTimer().getTime(TimeUnit.MILLISECONDS)
		));
	}

	public static GooGl gooGl(String content) {
		return new GooGl(content);
	}

	public static TinyURL tinyUrl(String content) {
		return new TinyURL(content);
	}
}