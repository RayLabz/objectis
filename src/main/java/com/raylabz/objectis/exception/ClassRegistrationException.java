package com.raylabz.objectis.exception;

public class ClassRegistrationException extends Exception {

    public ClassRegistrationException(Class<?> aClass) {
        super("The class '" + aClass.getName() + "' is not registered. Register your classes using Objectis.register().");
    }

    public ClassRegistrationException(String message) {
        super(message);
    }

}
