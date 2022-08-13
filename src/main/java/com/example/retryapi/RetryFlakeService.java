package com.example.retryapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class RetryFlakeService implements FlakeService {

    private final DirectFlakeService flakeService;
    private final RetryTemplate retryTemplate;

    @Autowired
    public RetryFlakeService(DirectFlakeService flakeService, RetryTemplate retryTemplate) {
        this.flakeService = flakeService;
        this.retryTemplate = retryTemplate;
    }

    @Override
    public FlakeResponse sendFlake(Flake flake) {
        log.info("start send flake");
        var startTime = Instant.now().toEpochMilli();

        var response = this.retryTemplate.execute(context -> {

            var retries = context.getRetryCount();

            log.info("{} retries {}", flake.getId(), retries);

            return this.flakeService.sendFlake(flake);
        }, context -> {
            // https://docs.spring.io/spring-retry/docs/api/current/org/springframework/retry/RecoveryCallback.html
            // we could throw from here, a different exception, that is caught
            // and is handled upstream, and returns the original Flake to its origin queue
            log.info("recovery callback");

            var endTime = Instant.now().toEpochMilli();
            var reason = "%s exceeded retries count %d in %d".formatted(flake.getId(), context.getRetryCount(), endTime - startTime);

            // we set reason to a useful string
            // we set the cause to the error (probably the 503) that the retry caught
            // and we set the flake
            // this way, upstream, we have as much as info as possible
            // to reset the origin queue into a state where it can try this again in a while
            var exception = new FlakeRetryException(reason, context.getLastThrowable(), flake);

            throw exception;
        });

        return response;
    }
}
