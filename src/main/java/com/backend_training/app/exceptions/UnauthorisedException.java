package com.backend_training.app.exceptions;

public class UnauthorisedException extends RuntimeException{
    public UnauthorisedException(String message) {
        super(message);
    }
}
