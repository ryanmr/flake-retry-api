package com.example.retryapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;

@Slf4j
public class RetryListener extends RetryListenerSupport {
    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        super.close(context, callback, throwable);

        log.info("listener close");
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        super.onError(context, callback, throwable);

        log.info("listener error");
    }

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        var proceed = super.open(context, callback);

        log.info("listener open: should proceed = {}", proceed);

        return proceed;
    }
}
