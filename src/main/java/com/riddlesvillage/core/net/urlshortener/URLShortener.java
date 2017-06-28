package com.riddlesvillage.core.net.urlshortener;

import com.google.common.util.concurrent.ListenableFuture;
import com.riddlesvillage.core.Messaging;
import com.riddlesvillage.core.service.ServiceExecutor;
import com.riddlesvillage.core.service.timer.TimedCallableTask;
import com.riddlesvillage.core.util.StringUtil;
import org.apache.commons.lang3.Validate;

import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class URLShortener extends TimedCallableTask<URL> {

    protected final String longUrl;

    private static final String[] removables = {
            // do not change order
            "ftp://" , "https://" , "http://"
    };

    public String getLongUrl() {
        return longUrl;
    }

    protected URLShortener(final String longUrl) {
        Validate.notNull(longUrl);
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
        return new GooGl(Validate.notNull(content));
    }

    public static TinyURL tinyUrl(String content) {
        return new TinyURL(Validate.notNull(content));
    }
}