package com.example.exception;

public class StudentIdAndIdMismatch extends RuntimeException {
    public StudentIdAndIdMismatch(String message) {
        super(message);
    }
}
