package com.riddlesvillage.core.net.paster;

import com.google.common.util.concurrent.ListenableFuture;
import com.riddlesvillage.core.Messaging;
import com.riddlesvillage.core.service.ServiceExecutor;
import com.riddlesvillage.core.service.timer.TimedCallableTask;
import org.apache.commons.lang3.Validate;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class Paster extends TimedCallableTask<URL> {

    private final String content;
    private final long timeout;
    private final TimeUnit unit;

    public Paster(String content) {
        this(content, 2, TimeUnit.SECONDS);
    }

    protected Paster(final String content,
                     final long timeout,
                     final TimeUnit unit) {
        this.content = Validate.notNull(content);
        this.timeout = timeout;
        this.unit = Validate.notNull(unit);
    }

    public String getContent() {
        return content;
    }

    public final URL paste() throws PasteException {
        ListenableFuture<URL> future = ServiceExecutor.getCachedExecutor().submit(this);
        try {
            return future.get(timeout, unit);
        } catch (InterruptedException | ExecutionException e) {
            throw new PasteException(e);
        } catch (TimeoutException e) {
            throw new PasteException("The request timed out after "
                    + timeout + unit.toString().toLowerCase());
        }
    }

    @Override
    public final URL call() throws Exception {
        return executeAndThen(() -> Messaging.debug(
                "Content pasted in %sms: %s",
                getTimer().getTime(TimeUnit.MILLISECONDS),
                getT()
        ));
    }

    public static Gist gist(final Map<String, String> files) {
        return new Gist(Validate.notNull(files));
    }

    public static Hastebin hastebin(String content) {
        return new Hastebin(Validate.notNull(content));
    }

    public static Pastebin pastebin(String content) {
        return new Pastebin(Validate.notNull(content));
    }
}