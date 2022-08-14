# spring retry

Spring Retry is a great feature. The `@Retryable` annotation is a great first step, but there's more you can do with a few extra pieces.

## RetryTemplate

RetryTemplate let's you perform the functionality in `@Retryable`, but at a more granular level. You might say at a more "imperative-level", but I argue this gives you more control.  With RetryTemplate, you can use _code_ to define and configure the behavior, instead of a bunch of annotation properties or configuration properties.

You can run `.execute`. The first argument is a callback, which comes with a context that has the internalized retry count, and also enables call-to-call attribute propagation. The second callback is the _RecoveryCallback_, called after the retry count is met. That callback is additionally useful, because you could propagate this failure back up the call chain, and restore whatever state there was before the original call was made. That might be db-state or queue-state, whatever works.

Ok, in the end, read the code. We use `Flake` as a domain object, but you can imagine it's an SMS or an Email or any other.. uh.. flakey activity.

## Log Example

```
2022-08-13 00:20:05.760  INFO 28282 --- [    Test worker] com.example.retryapi.FlakeServiceTest    : Starting FlakeServiceTest using Java 17.0.4 on Phi with PID 28282 (started by ryanrampersad in /Users/ryanrampersad/Code/general/spring/retryapi)
2022-08-13 00:20:05.761  INFO 28282 --- [    Test worker] com.example.retryapi.FlakeServiceTest    : No active profile set, falling back to 1 default profile: "default"
2022-08-13 00:20:06.302  INFO 28282 --- [    Test worker] com.example.retryapi.FlakeServiceTest    : Started FlakeServiceTest in 0.674 seconds (JVM running for 1.119)
2022-08-13 00:20:06.471  INFO 28282 --- [    Test worker] com.example.retryapi.BetterRetryFlakeService   : start send flake
2022-08-13 00:20:06.473  INFO 28282 --- [    Test worker] com.example.retryapi.BetterRetryFlakeService   : 123 retries 0
2022-08-13 00:20:06.473  INFO 28282 --- [    Test worker] com.example.retryapi.DirectFlakeService  : start send flake
2022-08-13 00:20:10.595 ERROR 28282 --- [    Test worker] com.example.retryapi.DirectFlakeService  : virtual upstream service unavailable because 0.650424 < 0.850000
2022-08-13 00:20:15.601  INFO 28282 --- [    Test worker] com.example.retryapi.BetterRetryFlakeService   : 123 retries 1
2022-08-13 00:20:15.602  INFO 28282 --- [    Test worker] com.example.retryapi.DirectFlakeService  : start send flake
2022-08-13 00:20:17.628 ERROR 28282 --- [    Test worker] com.example.retryapi.DirectFlakeService  : virtual upstream service unavailable because 0.539863 < 0.850000
2022-08-13 00:20:27.632  INFO 28282 --- [    Test worker] com.example.retryapi.BetterRetryFlakeService   : 123 retries 2
2022-08-13 00:20:27.633  INFO 28282 --- [    Test worker] com.example.retryapi.DirectFlakeService  : start send flake
2022-08-13 00:20:27.868 ERROR 28282 --- [    Test worker] com.example.retryapi.DirectFlakeService  : virtual upstream service unavailable because 0.777223 < 0.850000
2022-08-13 00:20:47.874  INFO 28282 --- [    Test worker] com.example.retryapi.BetterRetryFlakeService   : 123 retries 3
2022-08-13 00:20:47.875  INFO 28282 --- [    Test worker] com.example.retryapi.DirectFlakeService  : start send flake
2022-08-13 00:20:51.468 ERROR 28282 --- [    Test worker] com.example.retryapi.DirectFlakeService  : virtual upstream service unavailable because 0.834097 < 0.850000
2022-08-13 00:21:31.472  INFO 28282 --- [    Test worker] com.example.retryapi.BetterRetryFlakeService   : 123 retries 4
2022-08-13 00:21:31.477  INFO 28282 --- [    Test worker] com.example.retryapi.DirectFlakeService  : start send flake
2022-08-13 00:21:34.617 ERROR 28282 --- [    Test worker] com.example.retryapi.DirectFlakeService  : virtual upstream service unavailable because 0.546025 < 0.850000
2022-08-13 00:21:34.619  INFO 28282 --- [    Test worker] com.example.retryapi.BetterRetryFlakeService   : recovery callback

Unexpected exception thrown: com.example.retryapi.FlakeRetryException: 123 exceeded retries count 5 in 88149
org.opentest4j.AssertionFailedError: Unexpected exception thrown: com.example.retryapi.FlakeRetryException: 123 exceeded retries count 5 in 88149
	at app//org.junit.jupiter.api.AssertDoesNotThrow.createAssertionFailedError(AssertDoesNotThrow.java:83)
```
