package com.raylabz.objectis.query;

import com.raylabz.objectis.Objectis;
import com.raylabz.objectis.PathMaker;
import com.raylabz.objectis.Reflector;
import com.raylabz.objectis.Serializer;
import com.raylabz.objectis.concurrency.ArrayRange;
import com.raylabz.objectis.concurrency.GetManyCallable;
import com.raylabz.objectis.exception.ClassRegistrationException;
import com.raylabz.objectis.exception.OperationFailedException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.locks.Lock;

public class ObjectisCollection<T> {

    private static final Object lock = new Object();
    private final Class<T> aClass;
    private final String name;

    /**
     * Constructs a new collection.
     * @param aClass The collection class.
     * @param name The name of the collection.
     */
    public ObjectisCollection(Class<T> aClass, String name) {
        this.aClass = aClass;
        this.name = name;
    }

    /**
     * Retrieves the reference key of this collection.
     * @return Returns a byte[]
     */
    public final byte[] getReference() {
        return PathMaker.getCollectionPath(aClass, name);
    }

    /**
     * Retrieve the reference key of this collection as a string.
     * @return Returns a String.
     */
    public final String getReferenceAsString() {
        return new String(getReference());
    }

    /**
     * Adds an item to the collection.
     * @param item The item to add.
     * @throws OperationFailedException Thrown when the operation has failed.
     */
    public final void add(T item) throws OperationFailedException {
        synchronized (lock) {
            try {
                Reflector.checkClass(item.getClass());
                Objectis.getJedis().sadd(getReference(), Serializer.serializeKey(Reflector.getIDField(item)));
            } catch (NoSuchFieldException | IllegalAccessException | ClassRegistrationException e) {
                throw new OperationFailedException(e);
            }
        }
    }

    /**
     * Adds a list of items to the collection.
     * @param items A list of items to add.
     */
    public final void addAll(List<T> items) {
        synchronized (lock) {
            try {
                Reflector.checkClass(aClass);
                for (T item : items) {
                    Objectis.getJedis().sadd(getReference(), Serializer.serializeKey(Reflector.getIDField(item)));
                }
            } catch (NoSuchFieldException | IllegalAccessException | ClassRegistrationException e) {
                throw new OperationFailedException(e);
            }
        }
    }

    /**
     * Retrieve all items in the collection.
     * @return a List of items.
     * @throws OperationFailedException thrown when the operation has failed.
     */
    public final List<T> list() throws OperationFailedException {
        synchronized (lock) {
            try {
                Reflector.checkClass(aClass);
                final Set<byte[]> itemIDsAsBytesSet = Objectis.getJedis().smembers(getReference());
                final List<byte[]> itemIDsAsBytes = new ArrayList<>(itemIDsAsBytesSet);
                return Objectis.getManyWithBytes(aClass, itemIDsAsBytes);
            } catch (ClassRegistrationException e) {
                throw new OperationFailedException(e);
            }
        }
    }

    /**
     * Deletes an item from the collection.
     * @param item The item to delete.
     */
    public final void delete(T item) {
        synchronized (lock) {
            try {
                Reflector.checkClass(aClass);
                final String id = Reflector.getIDField(item);
                Objectis.getJedis().srem(getReference(), Serializer.serializeKey(id));
            } catch (ClassRegistrationException | NoSuchFieldException | IllegalAccessException e) {
                throw new OperationFailedException(e);
            }
        }
    }

    /**
     * Deletes an item from the collection.
     * @param itemID The ID of the item to delete.
     */
    public final  void delete(String itemID) {
        synchronized (lock) {
            try {
                Reflector.checkClass(aClass);
                Objectis.getJedis().srem(getReference(), Serializer.serializeKey(itemID));
            } catch (ClassRegistrationException e) {
                throw new OperationFailedException(e);
            }
        }
    }

    /**
     * Delete a list of items from the collection.
     * @param items The list of items to delete.
     */
    public final void deleteAll(List<T> items) {
        synchronized (lock) {
            try {
                Reflector.checkClass(aClass);
                for (T item : items) {
                    final String id = Reflector.getIDField(item);
                    Objectis.getJedis().srem(getReference(), Serializer.serializeKey(id));
                }
            } catch (ClassRegistrationException | NoSuchFieldException | IllegalAccessException e) {
                throw new OperationFailedException(e);
            }
        }
    }

    /**
     * Delete a list of items from the collection.
     * @param ids The IDs of the items to delete.
     */
    public final void deleteAll(String... ids) {
        synchronized (lock) {
            try {
                Reflector.checkClass(aClass);
                for (String id : ids) {
                    Objectis.getJedis().srem(getReference(), Serializer.serializeKey(id));
                }
            } catch (ClassRegistrationException e) {
                throw new OperationFailedException(e);
            }
        }
    }

    /**
     * Checks if an item exists in the collection.
     * @param item The item to check.
     * @return Returns true if the item is in the collection, false otherwise.
     */
    public final boolean contains(T item) {
        synchronized (lock) {
            try {
                Reflector.checkClass(aClass);
                final String id = Reflector.getIDField(item);
                return Objectis.getJedis().sismember(getReference(), Serializer.serializeKey(id));
            } catch (ClassRegistrationException | NoSuchFieldException | IllegalAccessException e) {
                throw new OperationFailedException(e);
            }
        }
    }

    /**
     * Checks if an item exists in the collection.
     * @param itemID The ID of the item to check.
     * @return Returns true if the item is in the collection, false otherwise.
     */
    public final boolean contains(String itemID) {
        synchronized (lock) {
            try {
                Reflector.checkClass(aClass);
                return Objectis.getJedis().sismember(getReference(), Serializer.serializeKey(itemID));
            } catch (ClassRegistrationException e) {
                throw new OperationFailedException(e);
            }
        }
    }

    /**
     * Filters the collection's items.
     * @return Returns an ObjectisFilterable.
     */
    public final ObjectisFilterable<T> filter() {
        synchronized (lock) {
            return new ObjectisFilterable<>(aClass, new Vector<>(list()));
        }
    }

}
