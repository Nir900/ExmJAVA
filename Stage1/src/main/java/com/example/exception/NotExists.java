package com.example.exception;

public class NotExists extends RuntimeException {
    public NotExists(String message) {
        super(message);
    }
}
