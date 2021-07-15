package com.raylabz.objectis.exception;

public class OperationFailedException extends RuntimeException {

    public OperationFailedException(String message) {
        super(message);
    }

    public OperationFailedException(Throwable cause) {
        super(cause);
    }

}
