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
import java.util.concurrent.TimeoutException;

public abstract class URLShortener extends TimedCallableTask<URL> {

    protected final String longUrl;
    protected final long timeout;
    protected final TimeUnit unit;

    private static final String[] removables = {
            // do not change order
            "ftp://" , "https://" , "http://"
    };

    public String getLongUrl() {
        return longUrl;
    }

    protected URLShortener(final String longUrl) {
        this(longUrl, 2, TimeUnit.SECONDS);
    }

    protected URLShortener(final String longUrl,
                           final long timeout,
                           final TimeUnit unit) {
        Validate.notNull(longUrl);
        for (String keyword : removables) {
            StringUtil.remove(longUrl, keyword);
        }

        this.longUrl = longUrl;
        this.timeout = timeout;
        this.unit = Validate.notNull(unit);
    }

    public final URL shorten() throws URLShortenerException {
        ListenableFuture<URL> future = ServiceExecutor.getCachedExecutor().submit(this);
        try {
            return future.get(timeout, unit);
        } catch (InterruptedException | ExecutionException e) {
            throw new URLShortenerException(e);
        } catch (TimeoutException e) {
            throw new URLShortenerException("The request timed out after "
                    + timeout + unit.toString().toLowerCase());
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