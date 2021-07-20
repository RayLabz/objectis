package com.raylabz.objectis;

import com.raylabz.objectis.concurrency.ArrayRange;
import com.raylabz.objectis.concurrency.GetManyCallable;
import com.raylabz.objectis.exception.ClassRegistrationException;
import com.raylabz.objectis.exception.OperationFailedException;
import com.raylabz.objectis.query.ObjectisCollection;
import com.raylabz.objectis.query.ObjectisFilterable;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class Objectis {

    private static Jedis jedis;
    private static int NUM_OF_PROCESSORS;
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
        NUM_OF_PROCESSORS = Runtime.getRuntime().availableProcessors();
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
        NUM_OF_PROCESSORS = Runtime.getRuntime().availableProcessors();
//        publisher = new Publisher(jedis);
//        publisher.start();
    }

    /**
     * Initializes Objectis.
     */
    public static void init() {
        jedis = new Jedis();
        NUM_OF_PROCESSORS = Runtime.getRuntime().availableProcessors();
//        publisher = new Publisher(jedis);
//        publisher.start();
    }

    /**
     * Initializes Objectis.
     * @param jedis A Jedis object.
     */
    public static void init(Jedis jedis) {
        Objectis.jedis = jedis;
        NUM_OF_PROCESSORS = Runtime.getRuntime().availableProcessors();
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
     * Stores a list of objects in the cache.
     * @param objects A list of objects to store.
     * @param <T> The type of objects.
     * @throws OperationFailedException when the operation fails.
     */
    public static <T> void createAll(List<T> objects) throws OperationFailedException {
        if (objects.size() > 0) {
            try {
                checkRegistration(objects.get(0).getClass());
                for (T object : objects) {
                    jedis.set(PathMaker.getObjectPath(object), Serializer.serializeObject(object));
                    final String idField = Reflector.getIDField(object);
                    jedis.sadd(PathMaker.getClassListPath(object.getClass()), idField.getBytes(StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                throw new OperationFailedException(e);
            }
        }
    }

    /**
     * Stores an object in the cache using a custom ID.
     * @param object The object to save.
     * @param id The custom ID.
     * @param <T> The type of object to store.
     * @throws OperationFailedException when the operation fails.
     */
    public static <T> void create(final T object, String id) throws OperationFailedException {
        try {
            checkRegistration(object);
            jedis.set(PathMaker.getObjectPath(object.getClass(), id), Serializer.serializeObject(object));
            jedis.sadd(PathMaker.getClassListPath(object.getClass()), id.getBytes(StandardCharsets.UTF_8));
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
     * Retrieve many objects, for a specific class from the cache using the IDs provided.
     * @param aClass The class.
     * @param ids A list of IDs.
     * @param <T> The object type.
     * @throws OperationFailedException when the operation cannot be completed.
     * @return Returns a list of objects.
     */
    public static <T> List<T> getMany(Class<T> aClass, String... ids) throws OperationFailedException {
        if (ids.length == 0) {
            return new ArrayList<>();
        }
        if (ids.length >= 50) {
            return getManyThreaded_MT(aClass, ids);
        }
        try {
            checkRegistration(aClass);
            byte[][] arrayOfIDs = new byte[ids.length][];
            for (int i = 0; i < ids.length; i++) {
                arrayOfIDs[i] = PathMaker.getObjectPath(aClass, ids[i]);
            }

            final List<byte[]> itemsBytes = jedis.mget(arrayOfIDs);
            ArrayList<T> items = new ArrayList<>();
            for (byte[] itemByte : itemsBytes) {
                final T item = Serializer.deserializeObject(itemByte, aClass);
                items.add(item);
            }
            return items;
        } catch (ClassRegistrationException e) {
            throw new OperationFailedException(e);
        }
    }

    public static <T> List<T> getManyWithBytes(Class<T> aClass, List<byte[]> ids) throws OperationFailedException {
        if (ids.size() == 0) {
            return new ArrayList<>();
        }
        if (ids.size() >= 50) {
            return getManyThreaded_MT(aClass, ids);
        }
        try {
            checkRegistration(aClass);
            byte[][] arrayOfIDs = new byte[ids.size()][];
            for (int i = 0; i < ids.size(); i++) {
                arrayOfIDs[i] = PathMaker.getObjectPath(aClass, new String(ids.get(i)));
            }

            final List<byte[]> itemsBytes = jedis.mget(arrayOfIDs);
            ArrayList<T> items = new ArrayList<>();
            for (byte[] itemByte : itemsBytes) {
                final T item = Serializer.deserializeObject(itemByte, aClass);
                items.add(item);
            }
            return items;
        } catch (ClassRegistrationException e) {
            throw new OperationFailedException(e);
        }
    }

    private static <T> List<T> getManyThreaded_MT(Class<T> aClass, String... ids) throws OperationFailedException {
        try {
            checkRegistration(aClass);
            byte[][] arrayOfIDs = new byte[ids.length][];
            for (int i = 0; i < ids.length; i++) {
                arrayOfIDs[i] = PathMaker.getObjectPath(aClass, ids[i]);
            }

            final List<byte[]> itemsBytes = jedis.mget(arrayOfIDs);

            final int numOfProcessors = Objectis.NUM_OF_PROCESSORS;

            final int itemsPerRange = itemsBytes.size() / numOfProcessors;
            final int remainder = itemsBytes.size() % numOfProcessors;
            int[] itemsInThreads = new int[numOfProcessors];
            for (int i = 0; i < itemsInThreads.length; i++) {
                itemsInThreads[i] = itemsPerRange;
                if (i < remainder) {
                    itemsInThreads[i]++;
                }
            }

            ArrayList<ArrayRange> arrayRanges = new ArrayList<>();

            int itemStart = 0;
            for (int i = 0; i < numOfProcessors; i++) {
                int itemEnd = itemStart + itemsInThreads[i];
                arrayRanges.add(new ArrayRange(itemStart, itemEnd));
                itemStart = itemEnd;
            }

            ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            List<GetManyCallable<T>> futureList = new ArrayList<>();
            for (ArrayRange arrayRange : arrayRanges) {
                GetManyCallable<T> callable = new GetManyCallable<>(aClass, itemsBytes, arrayRange);
                futureList.add(callable);
            }

            final List<Future<List<T>>> futures = service.invokeAll(futureList);
            service.shutdown();

            ArrayList<T> items = new ArrayList<>();

            for (Future<List<T>> future : futures) {
                final List<T> sublist = future.get();
                items.addAll(sublist);
            }

            return items;
        } catch (ClassRegistrationException | InterruptedException | ExecutionException e) {
            throw new OperationFailedException(e);
        }
    }

    static <T> List<T> getManyThreaded_MT(Class<T> aClass, List<byte[]> ids) throws OperationFailedException {
        try {
            checkRegistration(aClass);
            byte[][] arrayOfIDs = new byte[ids.size()][];
            for (int i = 0; i < ids.size(); i++) {
                arrayOfIDs[i] = PathMaker.getObjectPath(aClass, new String(ids.get(i)));
            }

            final List<byte[]> itemsBytes = jedis.mget(arrayOfIDs);

            final int numOfProcessors = Objectis.NUM_OF_PROCESSORS;

            final int itemsPerRange = itemsBytes.size() / numOfProcessors;
            final int remainder = itemsBytes.size() % numOfProcessors;
            int[] itemsInThreads = new int[numOfProcessors];
            for (int i = 0; i < itemsInThreads.length; i++) {
                itemsInThreads[i] = itemsPerRange;
                if (i < remainder) {
                    itemsInThreads[i]++;
                }
            }

            ArrayList<ArrayRange> arrayRanges = new ArrayList<>();

            int itemStart = 0;
            for (int i = 0; i < numOfProcessors; i++) {
                int itemEnd = itemStart + itemsInThreads[i];
                arrayRanges.add(new ArrayRange(itemStart, itemEnd));
                itemStart = itemEnd;
            }

            ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            List<GetManyCallable<T>> futureList = new ArrayList<>();
            for (ArrayRange arrayRange : arrayRanges) {
                GetManyCallable<T> callable = new GetManyCallable<>(aClass, itemsBytes, arrayRange);
                futureList.add(callable);
            }

            final List<Future<List<T>>> futures = service.invokeAll(futureList);
            service.shutdown();

            ArrayList<T> items = new ArrayList<>();

            for (Future<List<T>> future : futures) {
                final List<T> sublist = future.get();
                items.addAll(sublist);
            }

            return items;
        } catch (ClassRegistrationException | InterruptedException | ExecutionException e) {
            throw new OperationFailedException(e);
        }
    }

    /**
     * Retrieve many objects, for a specific class from the cache using the IDs provided.
     * @param aClass The class.
     * @param ids A list of IDs.
     * @param <T> The object type.
     * @throws OperationFailedException when the operation cannot be completed.
     * @return Returns a list of objects.
     */
    public static <T> List<T> getMany(Class<T> aClass, List<String> ids) throws OperationFailedException {
        return getMany(aClass, ids.toArray(new String[0]));
    }

    /**
     * Retrieves all objects stored for a particular class.
     * @param aClass The class of the objects to retrieve.
     * @param <T> The type of the objects.
     * @return Returns a list of objects.
     * @throws OperationFailedException thrown when a problem occurs with the operation.
     */
    public static <T> List<T> list(Class<T> aClass) throws OperationFailedException {
        try {
            checkRegistration(aClass);
            final Set<byte[]> classObjectBytes = jedis.smembers(PathMaker.getClassListPath(aClass));

            ArrayList<String> ids = new ArrayList<>();
            for (byte[] idBytes : classObjectBytes) {
                ids.add(new String(idBytes));
            }

            return getMany(aClass, ids);
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
     * Deletes a list of objects from the cache based on the given IDs.
     * @param aClass The object class.
     * @param ids A list of object IDs.
     * @param <T> The type of the object.
     * @throws OperationFailedException when the operation fails.
     */
    public static <T> void deleteAll(Class<T> aClass, String... ids) {
        try {
            checkRegistration(aClass);
            for (String id : ids) {
                final byte[] objectPathBytes = PathMaker.getObjectPath(aClass, id);
                jedis.del(objectPathBytes);
                jedis.srem(PathMaker.getClassListPath(aClass), Serializer.serializeKey(id));
//            publisher.publish(aClass, id, OperationType.DELETE, null);
            }
        } catch (Exception e) {
            throw new OperationFailedException(e);
        }
    }

    /**
     * Deletes a list of objects from the cache based on the given IDs.
     * @param aClass The object class.
     * @param ids A list of object IDs.
     * @param <T> The type of the object.
     * @throws OperationFailedException when the operation fails.
     */
    public static <T> void deleteAll(Class<T> aClass, List<String> ids) {
        deleteAll(aClass, ids.toArray(new String[0]));
    }

    /**
     * Deletes a list of objects from the cache.
     * @param objects The list of objects to delete.
     * @param <T> The type of the objects.
     * @throws OperationFailedException when the operation fails.
     */
    public static <T> void deleteAll(List<T> objects) throws OperationFailedException {
        if (objects.size() > 0) {
            try {
                checkRegistration(objects.get(0).getClass());
                for (T object : objects) {
                    final String id = Reflector.getIDField(object);
                    final byte[] objectPathBytes = PathMaker.getObjectPath(object.getClass(), id);
                    jedis.del(objectPathBytes);
                    jedis.srem(PathMaker.getClassListPath(object.getClass()), Serializer.serializeKey(id));
                }
            } catch (Exception e) {
                throw new OperationFailedException(e);
            }
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
     * Retrieves a reference to a collection.
     * @param aClass The class of the collection.
     * @param collectionName The collection's name
     * @param <T> The type.
     * @return Returns an ObjectisCollection.
     */
    public static <T> ObjectisCollection<T> collection(Class<T> aClass, String collectionName) {
        return new ObjectisCollection<>(aClass, collectionName);
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
