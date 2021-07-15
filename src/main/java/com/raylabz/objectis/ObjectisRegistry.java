package com.raylabz.objectis;

import com.raylabz.objectis.exception.ClassRegistrationException;

import java.util.HashSet;

public class ObjectisRegistry {

    private static final HashSet<Class<?>> REGISTERED_CLASSES = new HashSet<>();

    /**
     * Checks a class for a valid structure and registers it.
     * @param aClass The class to register.
     * @throws ClassRegistrationException Thrown when the class provided does not have a valid structure.
     */
    static void register(Class<?> aClass) throws ClassRegistrationException {
        Reflector.checkClass(aClass);
        REGISTERED_CLASSES.add(aClass);
    }

    /**
     * Checks if a class is registered.
     * @param aClass The class to check.
     * @return Returns true if the class provided is registered (and valid), false otherwise.
     */
    static boolean isRegistered(final Class<?> aClass) {
        return REGISTERED_CLASSES.contains(aClass);
    }

}
