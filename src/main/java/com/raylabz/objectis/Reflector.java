package com.raylabz.objectis;

import com.raylabz.objectis.annotation.ObjectisObject;
import com.raylabz.objectis.exception.ClassRegistrationException;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class Reflector {

    /**
     * Checks if the given class contains the required fields, types and annotations.
     * @param clazz The class to check.
     * @throws ClassRegistrationException a) when the 'id' field has not been declared, is not of type string and not annotated with @ID, b) when the class is not annotated with @FirestormObjectAnnotation.
     */
    public static void checkClass(final Class<?> clazz) throws ClassRegistrationException {
        //Check if object is annotated as @ObjectisObject:
        final ObjectisObject classAnnotation = clazz.getAnnotation(ObjectisObject.class);
        if (classAnnotation == null) {
            throw new ClassRegistrationException("The class '" + clazz.getSimpleName() + "' needs to be annotated with @" + ObjectisObject.class.getSimpleName() + ".");
        }

        //Check if class of object implements Serializable:
        if (!Serializable.class.isAssignableFrom(clazz)) {
            throw new ClassRegistrationException("The class '" + clazz.getSimpleName() + "' needs to implement the Serializable interface.");
        }

        Field idField = null;
        Class<?> idFieldType = null;

        //Check if the field 'id' exists at all:
        try {
            idField = clazz.getDeclaredField("id");
            idFieldType = idField.getType();
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            while (superClass.getSuperclass() != null) {
                final Field[] superClassFields = superClass.getDeclaredFields();
                for (Field f : superClassFields) {
                    if (f.getName().equals("id")) {
                        idField = f;
                        idFieldType = f.getType();
                        break;
                    }
                }

                if (idField != null) {
                    break;
                }
                else {
                    superClass = superClass.getSuperclass();
                }
            }
            if (idField == null) {
                throw new ClassRegistrationException("A field named 'id' of type String needs to exist in class '" + clazz.getSimpleName() + "' or its parent classes but was not found.");
            }
        }

        //Check if field 'id' is String:
        if (idFieldType != String.class) {
            throw new ClassRegistrationException("The 'id' field of class '" + clazz.getSimpleName() + "' must be of type String, but type " + idFieldType.getSimpleName() + " found.");
        }

        //Check if the 'id' field has a public getter:
        if (!fieldHasPublicGetter(idField, clazz)) {
            throw new ClassRegistrationException("The 'id' field of class '" + clazz.getSimpleName() + "' does not have a getter method called '" + Reflector.getGetterMethodName(idField) + "'.");
        }

    }

    /**
     * Checks if a given field has a public getter method.
     * @param field The field to check for a getter method.
     * @param clazz The class of the field.
     * @return Returns true if a public getter was found for this field, false otherwise.
     */
    private static boolean fieldHasPublicGetter(final Field field, final Class<?> clazz) {
        final String getterMethodName = getGetterMethodName(field);
        final Method[] publicMethods = clazz.getMethods();
        for (Method m : publicMethods) {
            if (
                    m.getName().equals(getterMethodName) && m.getReturnType().equals(field.getType()) ||
                            m.getName().equals(getterMethodName) && (m.getReturnType().equals(boolean.class) || m.getReturnType().equals(Boolean.class))
            ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the Java convention-based getter method name for a given field.
     * @param field The field to retrieve the getter method name for.
     * @return Returns the getter method name for the specified field.
     */
    private static String getGetterMethodName(final Field field) {
        if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
            return "is" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        }
        else {
            return "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        }
    }

    /**
     * Retrieves the ID value of an object.
     * @param object The object to retrieve the ID value of.
     * @return Returns a the ID of the object as a string.
     * @throws NoSuchFieldException Thrown when the field 'id' cannot be accessed.
     * @throws IllegalAccessException Thrown when the field 'id' cannot be accessed.
     */
    public static String getIDField(final Object object) throws NoSuchFieldException, IllegalAccessException {
        Field idField;
        try {
            idField = object.getClass().getDeclaredField("id");
        } catch (NoSuchFieldException e) {
            idField = findUnderlyingIDField(object.getClass());
        }
        if (idField != null) {
            boolean accessible = idField.isAccessible();
            idField.setAccessible(true);
            final String value = (String) idField.get(object);
            idField.setAccessible(accessible);
            return value;
        }
        else {
            throw new NoSuchFieldException();
        }
    }

    /**
     * Finds an underlying ID fields from a superclass provided.
     * @param clazz The superclass.
     * @return Returns a field.
     */
    static Field findUnderlyingIDField(Class<?> clazz) {
        Class<?> current = clazz;
        do {
            try {
                return current.getDeclaredField("id");
            } catch (Exception ignored) {}
        } while((current = current.getSuperclass()) != null);
        return null;
    }

}
