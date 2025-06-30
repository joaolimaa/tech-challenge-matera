package com.example.materabank.core.exception;

public class GatewayException extends RuntimeException {
    public GatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}
