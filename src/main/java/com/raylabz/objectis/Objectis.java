package com.raylabz.objectis;

import com.raylabz.objectis.exception.ClassRegistrationException;
import com.raylabz.objectis.exception.OperationFailedException;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public final class Objectis {

    private static Jedis jedis;

    private Objectis() { }

    /**
     * Initializes Objectis.
     * @param host The host name (typically an IP address)
     * @param port The port
     * @param timeout The timeout in ms.
     * @param ssl Use SSL
     */
    public static void init(String host, int port, int timeout, boolean ssl) {
        jedis = new Jedis(host, port, timeout, ssl);
    }

    /**
     * Initializes Objectis.
     * @param host The host name (typically an IP address)
     * @param port The port
     */
    public static void init(String host, int port) {
        jedis = new Jedis(host, port);
    }

    /**
     * Initializes Objectis.
     */
    public static void init() {
        jedis = new Jedis();
    }

    /**
     * Initializes Objectis.
     * @param jedis A Jedis object.
     */
    public static void init(Jedis jedis) {
        Objectis.jedis = jedis;
    }

    /**
     * Checks if an object's class is registered.
     * @param object The object to check the class of.
     * @throws ClassRegistrationException Thrown when the object's class is not registered.
     */
    static void checkRegistration(Object object) throws ClassRegistrationException {
        if (!ObjectisRegistry.isRegistered(object.getClass())) {
            throw new ClassRegistrationException(object.getClass());
        }
    }

    /**
     * Checks if a class is registered.
     * @param aClass The class to check.
     * @throws ClassRegistrationException Thrown when the class is not registered.
     */
    static void checkRegistration(Class<?> aClass) throws ClassRegistrationException {
        if (!ObjectisRegistry.isRegistered(aClass)) {
            throw new ClassRegistrationException(aClass);
        }
    }

    /**
     * Registers a class.
     * @param aClass The class to register.
     * @throws ClassRegistrationException Thrown when the class cannot be registered.
     */
    public static void register(Class<?> aClass) throws ClassRegistrationException {
        ObjectisRegistry.register(aClass);
    }

    /**
     * Flushes the cache.
     */
    public static void flush() {
        jedis.flushDB();
    }

    /**
     * Stores an object in the cache.
     * @param object The object to save.
     * @param <T> The type of object to store.
     * @throws OperationFailedException when the operation fails.
     */
    public static <T> void create(final T object) throws OperationFailedException {
        try {
            checkRegistration(object);
            jedis.set(PathMaker.getObjectPath(object), Serializer.serializeObject(object));
            jedis.sadd(PathMaker.getClassListPath(object.getClass()), Serializer.serializeObject(object));
        } catch (IllegalAccessException | NoSuchFieldException | ClassRegistrationException e) {
            throw new OperationFailedException(e);
        }
    }

    /**
     * Updates an object in the cache.
     * @param object The object to update.
     * @param <T> The type of the object
     * @throws OperationFailedException thrown when the operation fails.
     */
    public static <T> void update(final T object) throws OperationFailedException {
        try {
            create(object);
        } catch (OperationFailedException e) {
            throw new OperationFailedException(e);
        }
    }

    /**
     * Retrieves an object from the cache.
     * @param aClass The class of the object.
     * @param id The object's ID.
     * @param <T> The type of the object.
     * @return Returns an object.
     * @throws OperationFailedException thrown when a problem occurs with the operation.
     */
    public static <T> T get(Class<T> aClass, String id) throws OperationFailedException {
        try {
            checkRegistration(aClass);
            final byte[] bytes = jedis.get(PathMaker.getObjectPath(aClass, id));
            return Serializer.deserializeObject(bytes, aClass);
        } catch (ClassRegistrationException | NoSuchFieldException | IllegalAccessException e) {
            throw new OperationFailedException(e);
        }
    }

    /**
     * Retrieves all objects stored for a particular class.
     * @param aClass The class of the objects to retrieve.
     * @param <T> The type of the objects.
     * @return Returns a collection of objects.
     * @throws OperationFailedException thrown when a problem occurs with the operation.
     */
    public static <T> Collection<T> list(Class<T> aClass) throws OperationFailedException {
        try {
            checkRegistration(aClass);
            final Set<byte[]> classObjectBytes = jedis.smembers(PathMaker.getClassListPath(aClass));
            ArrayList<T> items = new ArrayList<>();
            for (byte[] idBytes : classObjectBytes) {
                String id = new String(idBytes);
                final byte[] objectPathBytes = PathMaker.getObjectPath(aClass, id);
                final byte[] objectBytes = jedis.get(objectPathBytes);
                final T object = Serializer.deserializeObject(objectBytes, aClass);
                items.add(object);
            }
            return items;
        } catch (ClassRegistrationException | NoSuchFieldException | IllegalAccessException e) {
            throw new OperationFailedException(e.getMessage());
        }
    }

    /**
     * Checks if an object exists in the cache.
     * @param aClass The class of the object.
     * @param id The ID of the object.
     * @param <T> The object type.
     * @throws OperationFailedException when the operation fails.
     * @return Returns true if the object exists, false otherwise.
     */
    public static <T> boolean exists(Class<T> aClass, String id) {
        try {
            checkRegistration(aClass);
            final byte[] objectPathBytes = PathMaker.getObjectPath(aClass, id);
            return jedis.get(objectPathBytes) != null;
        } catch (NoSuchFieldException | IllegalAccessException | ClassRegistrationException e) {
            throw new OperationFailedException(e);
        }
    }

    /**
     * Deletes an object from the cache.
     * @param aClass The object class.
     * @param id The ID of the object.
     * @param <T> The type of the object.
     * @throws OperationFailedException when the operation fails.
     */
    public static <T> void delete(Class<T> aClass, String id) {
        try {
            checkRegistration(aClass);
            final byte[] objectPathBytes = PathMaker.getObjectPath(aClass, id);
            jedis.del(objectPathBytes);
        } catch (ClassRegistrationException | NoSuchFieldException | IllegalAccessException e) {
            throw new OperationFailedException(e);
        }
    }

    /**
     * Deletes an object from the cache.
     * @param object The object to delete.
     * @param <T> The type of the object.
     * @throws OperationFailedException when the operation fails.
     */
    public static <T> void delete(T object) throws OperationFailedException {
        try {
            checkRegistration(object);
            final String id = Reflector.getIDField(object);
            final byte[] objectPathBytes = PathMaker.getObjectPath(object.getClass(), id);
            jedis.del(objectPathBytes);
        } catch (ClassRegistrationException | NoSuchFieldException | IllegalAccessException e) {
            throw new OperationFailedException(e);
        }
    }

}