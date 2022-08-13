package com.example.retryapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;


@Slf4j
@Service
public class DirectFlakeService implements FlakeService {

    /**
     * Construct a situation where there's some latency and simulate
     * a response that comes back sometimes, and at othertimes responds with a 503 (upstream unavailable).
     * @param flake a flake, with basic properties
     * @return a flake response, with basic properties
     */
    @Override
    public FlakeResponse sendFlake(Flake flake) {
        log.info("start send flake");

        try {
            Thread.sleep((long) (Math.random() * 5 * 1000));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        // tune this variable for how often upstream should be unavailable
        var failRatio = .85;
        var failRandom = Math.random();
        var shouldFail = failRandom < failRatio;

        // throw 503
        if (shouldFail) {
            var reason = "virtual upstream service unavailable because %f < %f".formatted(failRandom, failRatio);
            log.error(reason);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, reason);
        }

        // simulate success; make a flake response
        var flakeResponse = FlakeResponse.builder()
                .id(flake.getId())
                .type(flake.getType())
                .receiptCode(UUID.randomUUID().toString())
                .build();

        log.info("flake response obtained");
        return flakeResponse;
    }

}
