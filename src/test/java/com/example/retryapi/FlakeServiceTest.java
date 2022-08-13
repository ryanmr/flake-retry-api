package com.example.retryapi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FlakeServiceTest {

    @Autowired
    RetryFlakeService flakeService;

    @DisplayName("send flake test, does not throw")
    @Test
    void testSendFlake() {
        var flake = Flake.builder().id("123").type("grizzly").build();

        assertDoesNotThrow( () -> {
            var response = flakeService.sendFlake(flake);
        });
    }
}
