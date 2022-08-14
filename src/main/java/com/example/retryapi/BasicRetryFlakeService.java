package com.example.retryapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class BasicRetryFlakeService implements FlakeService {

    private final DirectFlakeService flakeService;


    @Autowired
    public BasicRetryFlakeService(DirectFlakeService flakeService) {
        this.flakeService = flakeService;
    }

    @Retryable(recover = "recoverFromSendFlake",
            maxAttempts = 5,
            backoff = @Backoff(multiplier = 2, maxDelay = 5 * 60 * 1000L))
    @Override
    public FlakeResponse sendFlake(Flake flake) {
        log.info("start send flake");
        var response = this.flakeService.sendFlake(flake);
        return response;
    }

    @Recover()
    private FlakeResponse recoverFromSendFlake(RuntimeException rt, Flake flake) {
        log.info("recovery callback");

        var reason = "%s exceeded retries count".formatted(flake.getId());

        var exception = new FlakeRetryException(reason, rt, flake);

        throw exception;
    }
}
