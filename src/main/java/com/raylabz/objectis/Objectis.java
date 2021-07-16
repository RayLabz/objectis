package com.raylabz.objectis;

import com.raylabz.objectis.exception.ClassRegistrationException;
import com.raylabz.objectis.exception.OperationFailedException;
//import com.raylabz.objectis.pubsub.OperationType;
//import com.raylabz.objectis.pubsub.Publisher;
import com.raylabz.objectis.query.ObjectisFilterable;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;

public final class Objectis {

    private static Jedis jedis;
//    private static Publisher publisher;

    /**
     * Private constructor.
     */
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
//        publisher = new Publisher(jedis);
//        publisher.start();
    }

    /**
     * Initializes Objectis.
     * @param host The host name (typically an IP address)
     * @param port The port
     */
    public static void init(String host, int port) {
        jedis = new Jedis(host, port);
//        publisher = new Publisher(jedis);
//        publisher.start();
    }

    /**
     * Initializes Objectis.
     */
    public static void init() {
        jedis = new Jedis();
//        publisher = new Publisher(jedis);
//        publisher.start();
    }

    /**
     * Initializes Objectis.
     * @param jedis A Jedis object.
     */
    public static void init(Jedis jedis) {
        Objectis.jedis = jedis;
//        publisher = new Publisher(jedis);
//        publisher.start();
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
            final String idField = Reflector.getIDField(object);
            jedis.sadd(PathMaker.getClassListPath(object.getClass()), idField.getBytes(StandardCharsets.UTF_8));
//            publisher.publish(object.getClass(), idField, OperationType.CREATE, object);
        } catch (Exception e) {
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
            checkRegistration(object);
            jedis.set(PathMaker.getObjectPath(object), Serializer.serializeObject(object));
            final String idField = Reflector.getIDField(object);
            jedis.sadd(PathMaker.getClassListPath(object.getClass()), idField.getBytes(StandardCharsets.UTF_8));
//            publisher.publish(object.getClass(), idField, OperationType.UPDATE, object);
        } catch (Exception e) {
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
            if (bytes == null) {
                return null;
            }
            return Serializer.deserializeObject(bytes, aClass);
        } catch (Exception e) {
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
        } catch (Exception e) {
            throw new OperationFailedException(e);
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
        } catch (Exception e) {
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
            jedis.srem(PathMaker.getClassListPath(aClass), Serializer.serializeKey(id));
//            publisher.publish(aClass, id, OperationType.DELETE, null);
        } catch (Exception e) {
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
            jedis.srem(PathMaker.getClassListPath(object.getClass()), Serializer.serializeKey(id));
        } catch (Exception e) {
            throw new OperationFailedException(e);
        }
    }

    /**
     * Enables filtering of a particular type of object.
     * @param aClass The type of object.
     * @param <T> The type of object.
     * @return Returns an ObjectisFilterable.
     */
    public static <T> ObjectisFilterable<T> filter(Class<T> aClass) {
        return new ObjectisFilterable<>(aClass);
    }

    /**
     * Enables filtering of a particular type of object using existing items.
     * @param aClass The type of object.
     * @param items The existing items.
     * @param <T> The type of object.
     * @return Returns an ObjectisFilterable.
     */
    public static <T> ObjectisFilterable<T> filter(Class<T> aClass, Vector<T> items) {
        return new ObjectisFilterable<>(aClass, items);
    }

    /**
     * Retrieve the Jedis object.
     * @return Returns a Jedis object.
     */
    public static Jedis getJedis() {
        return jedis;
    }

//    public static <T> void addListener(Class<T> aClass, String id) {
//
//    }

}
