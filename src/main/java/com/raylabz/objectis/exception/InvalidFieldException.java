package com.raylabz.objectis.exception;

public class InvalidFieldException extends RuntimeException {

    public InvalidFieldException(String message) {
        super(message);
    }

    public InvalidFieldException(Throwable cause) {
        super(cause);
    }

}
