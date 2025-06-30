package com.example.materabank.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicatedRegisterException extends RuntimeException {
    public DuplicatedRegisterException(String message) {
        super(message);
    }
}
