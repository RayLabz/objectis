package com.raylabz.objectis;

import java.nio.charset.StandardCharsets;

public class PathMaker {

    /**
     * Constructs an object path as a string and returns its bytes: #CLASS_NAME#/#ID#
     * @param object The object to get the path for.
     * @return Returns a byte[]
     * @throws NoSuchFieldException thrown when the object does not have an ID field.
     * @throws IllegalAccessException thrown when the ID field cannot be accessed.
     */
    static byte[] getObjectPath(Object object) throws NoSuchFieldException, IllegalAccessException {
        return Serializer.serializeKey(object.getClass().getName() + "/" + Reflector.getIDField(object));
    }

    /**
     * Constructs an object path as a string based on a class and ID given and returns its bytes: #CLASS_NAME#/#ID#
     * @param aClass The class.
     * @param id The ID
     * @return Returns a byte[]
     * @throws NoSuchFieldException thrown when the object does not have an ID field.
     * @throws IllegalAccessException thrown when the ID field cannot be accessed.
     */
    static byte[] getObjectPath(Class<?> aClass, String id) throws NoSuchFieldException, IllegalAccessException {
        return Serializer.serializeKey(aClass.getName() + "/" + id);
    }

    /**
     * Constructs a class list path as a string and returns its bytes: #CLASS_NAME#
     * @param aClass The class.
     * @return Returns a byte[].
     */
    static byte[] getClassListPath(Class<?> aClass) {
        return Serializer.serializeKey(aClass.getName());
    }

}
