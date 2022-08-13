package com.example.retryapi;

import lombok.Data;

@Data
public class FlakeRetryException extends RuntimeException {
    private Flake flake;

    public FlakeRetryException(String message, Throwable cause, Flake flake) {
        super(message, cause);
        this.flake = flake;
    }
}
