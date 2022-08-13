package com.example.retryapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableRetry
public class RetryConfig {

    //...
    @Bean
    public RetryTemplate retryTemplate() {
        // 5 minutes seems like the longest time we'd want to keep
        // method calls sitting around somewhere;
        // and if a service hasn't recovered in that time,
        // it's probably much more down that a basic retry can handle
        var maxInterval = 5 * 60 * 1000L;

        // 5 seconds seems like an OK initial range
        // and with the default 2x multiplier, you get
        // 5s, 10s, 20s, 40s, 1m20s, 2m40s, 5m20s
        var initialInterval = 5 * 1000L;

        // https://docs.spring.io/spring-retry/docs/api/current/org/springframework/retry/backoff/ExponentialBackOffPolicy.html
        // there are other policies;
        // https://docs.spring.io/spring-retry/docs/api/current/org/springframework/retry/backoff/BackOffPolicy.html
        // we could even implement our own fibonacci variant
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setMaxInterval(maxInterval);
        backOffPolicy.setInitialInterval(initialInterval);

        // there are various policies
        // perhaps the circuit breaker policy could be useful
        // https://docs.spring.io/spring-retry/docs/api/current/org/springframework/retry/RetryPolicy.html
        // in general the simple policy is just a counter
        // and 5 tries seems fine
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(5);


        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }

}

