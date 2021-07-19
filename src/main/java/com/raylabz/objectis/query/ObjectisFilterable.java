package com.raylabz.objectis.query;

import com.raylabz.objectis.Objectis;
import com.raylabz.objectis.Reflector;
import com.raylabz.objectis.Serializer;
import com.raylabz.objectis.concurrency.ArrayRange;
import com.raylabz.objectis.concurrency.FilterCallable;
import com.raylabz.objectis.concurrency.FilterCallableProcessor;
import com.raylabz.objectis.concurrency.GetManyCallable;
import com.raylabz.objectis.exception.InvalidFieldException;
import com.raylabz.objectis.exception.OperationFailedException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Provides functionality to filter items in the cache.
 * @param <T> The type of item
 */
public class ObjectisFilterable<T> {

    private final Class<T> aClass;
    private final Field[] classFields;

    private Vector<T> temporaryItems;

    /**
     * Constructs a filterable
     * @param aClass The type of objects this filterable works on.
     */
    public ObjectisFilterable(Class<T> aClass) {
        this.aClass = aClass;
        temporaryItems = new Vector<>(Objectis.list(aClass));
        classFields = aClass.getDeclaredFields();
    }

    /**
     * Constructs a filterable using an existing list of items.
     * @param aClass The class.
     * @param items A list of existing items.
     */
    public ObjectisFilterable(Class<T> aClass, Vector<T> items) {
        this.aClass = aClass;
        temporaryItems = items;
        classFields = aClass.getDeclaredFields();
    }

    /**
     * Filters by a field's value being equal to the one provided.
     * @param fieldName The field name.
     * @param value The value provided.
     * @return Returns a filterable.
     * @throws InvalidFieldException thrown when the field cannot be accessed or does not exist.
     */
    public ObjectisFilterable<T> whereEqualTo(String fieldName, Object value) throws InvalidFieldException {
        final Field field = getField(fieldName);
        if (field == null) {
            throw new InvalidFieldException("The field '" + fieldName + "' does not exist in class '" + aClass.getSimpleName() + "'.");
        }

        boolean preAccessible = field.isAccessible();
        field.setAccessible(true);

        try {
            ArrayList<T> itemsToRemove = new ArrayList<>();
            for (T temporaryItem : temporaryItems) {
                final Object objectValue = field.get(temporaryItem);
                if (!objectValue.equals(value)) {
                    itemsToRemove.add(temporaryItem);
                }
            }

            for (T item : itemsToRemove) {
                temporaryItems.remove(item);
            }

        } catch (IllegalAccessException e) {
            throw new InvalidFieldException(e);
        }

        field.setAccessible(preAccessible);
        return this;
    }

    /**
     * Filters by a field's value being not equal to the one provided.
     * @param fieldName The field name.
     * @param value The value provided.
     * @return Returns a filterable.
     * @throws InvalidFieldException thrown when the field cannot be accessed or does not exist.
     */
    public ObjectisFilterable<T> whereNotEqualTo(String fieldName, Object value) throws InvalidFieldException {
        final Field field = getField(fieldName);
        if (field == null) {
            throw new InvalidFieldException("The field '" + fieldName + "' does not exist in class '" + aClass.getSimpleName() + "'.");
        }

        try {
            ArrayList<T> itemsToRemove = new ArrayList<>();
            for (T temporaryItem : temporaryItems) {
                boolean preAccessible = field.isAccessible();
                field.setAccessible(true);
                final Object objectValue = field.get(temporaryItem);
                field.setAccessible(preAccessible);
                if (objectValue.equals(value)) {
                    itemsToRemove.add(temporaryItem);
                }
            }

            for (T item : itemsToRemove) {
                temporaryItems.remove(item);
            }

        } catch (IllegalAccessException e) {
            throw new InvalidFieldException(e);
        }

        return this;
    }

    /**
     * Filters by a field's value being greater than the one provided.
     * @param fieldName The field name.
     * @param value The value provided.
     * @param <Y> The type of comparable object to use.
     * @return Returns a filterable.
     * @throws InvalidFieldException thrown when the field cannot be accessed or does not exist.
     */
    public <Y extends Comparable<?>> ObjectisFilterable<T> whereGreaterThan(String fieldName, Y value) throws InvalidFieldException {
        final Field field = getField(fieldName);
        if (field == null) {
            throw new InvalidFieldException("The field '" + fieldName + "' does not exist in class '" + aClass.getSimpleName() + "'.");
        }

        try {
            ArrayList<T> itemsToRemove = new ArrayList<>();
            for (T temporaryItem : temporaryItems) {
                boolean preAccessible = field.isAccessible();
                field.setAccessible(true);
                final Object objectValue = field.get(temporaryItem);
                final Comparable<Y> castedObject = value.getClass().cast(objectValue);
                field.setAccessible(preAccessible);
                if (castedObject.compareTo(value) < 1) {
                    itemsToRemove.add(temporaryItem);
                }
            }

            for (T item : itemsToRemove) {
                temporaryItems.remove(item);
            }

        } catch (IllegalAccessException | ClassCastException e) {
            throw new InvalidFieldException(e);
        }

        return this;
    }

    /**
     * Filters by a field's value being greater than or equal to the one provided.
     * @param fieldName The field name.
     * @param value The value provided.
     * @param <Y> The type of comparable object to use.
     * @return Returns a filterable.
     * @throws InvalidFieldException thrown when the field cannot be accessed or does not exist.
     */
    public <Y extends Comparable<?>> ObjectisFilterable<T> whereGreaterThanOrEqualTo(String fieldName, Y value) throws InvalidFieldException {
        final Field field = getField(fieldName);
        if (field == null) {
            throw new InvalidFieldException("The field '" + fieldName + "' does not exist in class '" + aClass.getSimpleName() + "'.");
        }

        try {
            ArrayList<T> itemsToRemove = new ArrayList<>();
            for (T temporaryItem : temporaryItems) {
                boolean preAccessible = field.isAccessible();
                field.setAccessible(true);
                final Object objectValue = field.get(temporaryItem);
                final Comparable<Y> castedObject = value.getClass().cast(objectValue);
                field.setAccessible(preAccessible);
                if (castedObject.compareTo(value) < 0) {
                    itemsToRemove.add(temporaryItem);
                }
            }

            for (T item : itemsToRemove) {
                temporaryItems.remove(item);
            }

        } catch (IllegalAccessException | ClassCastException e) {
            throw new InvalidFieldException(e);
        }

        return this;
    }

    /**
     * Filters by a field's value being less than the one provided.
     * @param fieldName The field name.
     * @param value The value provided.
     * @param <Y> The type of comparable object to use.
     * @return Returns a filterable.
     * @throws InvalidFieldException thrown when the field cannot be accessed or does not exist.
     */
    public <Y extends Comparable<?>> ObjectisFilterable<T> whereLessThan(String fieldName, Y value) throws InvalidFieldException {
        final Field field = getField(fieldName);
        if (field == null) {
            throw new InvalidFieldException("The field '" + fieldName + "' does not exist in class '" + aClass.getSimpleName() + "'.");
        }

        try {
            ArrayList<T> itemsToRemove = new ArrayList<>();
            for (T temporaryItem : temporaryItems) {
                boolean preAccessible = field.isAccessible();
                field.setAccessible(true);
                final Object objectValue = field.get(temporaryItem);
                final Comparable<Y> castedObject = value.getClass().cast(objectValue);
                field.setAccessible(preAccessible);
                if (castedObject.compareTo(value) > -1) {
                    itemsToRemove.add(temporaryItem);
                }
            }

            for (T item : itemsToRemove) {
                temporaryItems.remove(item);
            }

        } catch (IllegalAccessException | ClassCastException e) {
            throw new InvalidFieldException(e);
        }

        return this;
    }

    /**
     * Filters by a field's value being less than or equal to the one provided.
     * @param fieldName The field name.
     * @param value The value provided.
     * @param <Y> The type of comparable object to use.
     * @return Returns a filterable.
     * @throws InvalidFieldException thrown when the field cannot be accessed or does not exist.
     */
    public <Y extends Comparable<?>> ObjectisFilterable<T> whereLessThanOrEqualTo(String fieldName, Y value) throws InvalidFieldException {
        final Field field = getField(fieldName);
        if (field == null) {
            throw new InvalidFieldException("The field '" + fieldName + "' does not exist in class '" + aClass.getSimpleName() + "'.");
        }

        try {
            ArrayList<T> itemsToRemove = new ArrayList<>();
            for (T temporaryItem : temporaryItems) {
                boolean preAccessible = field.isAccessible();
                field.setAccessible(true);
                final Object objectValue = field.get(temporaryItem);
                final Comparable<Y> castedObject = value.getClass().cast(objectValue);
                field.setAccessible(preAccessible);
                if (castedObject.compareTo(value) > 0) {
                    itemsToRemove.add(temporaryItem);
                }
            }

            for (T item : itemsToRemove) {
                temporaryItems.remove(item);
            }

        } catch (IllegalAccessException | ClassCastException e) {
            throw new InvalidFieldException(e);
        }

        return this;
    }

    /**
     * Filters by an array field containing a specific value.
     * @param fieldName The field name.
     * @param value The value provided.
     * @return Returns a filterable.
     * @throws InvalidFieldException thrown when the field cannot be accessed or does not exist.
     */
    public ObjectisFilterable<T> whereArrayContains(String fieldName, Object value) throws InvalidFieldException {
        final Field field = getField(fieldName);
        if (field == null) {
            throw new InvalidFieldException("The field '" + fieldName + "' does not exist in class '" + aClass.getSimpleName() + "'.");
        }

        try {
            ArrayList<T> itemsToRemove = new ArrayList<>();
            for (T temporaryItem : temporaryItems) {
                boolean preAccessible = field.isAccessible();
                field.setAccessible(true);
                final Object objectValue = field.get(temporaryItem);
                field.setAccessible(preAccessible);
                final Collection<?> collectionObject = (Collection<?>) objectValue;
                if (!collectionObject.contains(value)) {
                    itemsToRemove.add(temporaryItem);
                }
            }

            for (T item : itemsToRemove) {
                temporaryItems.remove(item);
            }

        } catch (IllegalAccessException | ClassCastException e) {
            throw new InvalidFieldException(e);
        }

        return this;
    }

    /**
     * Filters by an array field containing any of a given list of values.
     * @param fieldName The field name.
     * @param values A list of values.
     * @return Returns a filterable.
     * @throws InvalidFieldException thrown when the field cannot be accessed or does not exist.
     */
    public ObjectisFilterable<T> whereArrayContainsAny(String fieldName, List<?> values) throws InvalidFieldException {
        final Field field = getField(fieldName);
        if (field == null) {
            throw new InvalidFieldException("The field '" + fieldName + "' does not exist in class '" + aClass.getSimpleName() + "'.");
        }

        try {
            ArrayList<T> itemsToRemove = new ArrayList<>();
            for (T temporaryItem : temporaryItems) {
                boolean preAccessible = field.isAccessible();
                field.setAccessible(true);
                final Object objectValue = field.get(temporaryItem);
                field.setAccessible(preAccessible);
                final Collection<?> collectionObject = (Collection<?>) objectValue;
                boolean containsAny = false;
                for (Object givenValue : values) {
                    if (collectionObject.contains(givenValue)) {
                        containsAny = true;
                        break;
                    }
                }
                if (!containsAny) {
                    itemsToRemove.add(temporaryItem);
                }
            }

            for (T item : itemsToRemove) {
                temporaryItems.remove(item);
            }

        } catch (IllegalAccessException | ClassCastException e) {
            throw new InvalidFieldException(e);
        }

        return this;
    }

    /**
     * Filters by an array field containing any of a given list of values.
     * @param fieldName The field name.
     * @param values A list of values.
     * @return Returns a filterable.
     * @throws InvalidFieldException thrown when the field cannot be accessed or does not exist.
     */
    public ObjectisFilterable<T> whereArrayContainsAny(String fieldName, Object... values) throws InvalidFieldException {
        final Field field = getField(fieldName);
        if (field == null) {
            throw new InvalidFieldException("The field '" + fieldName + "' does not exist in class '" + aClass.getSimpleName() + "'.");
        }

        try {
            ArrayList<T> itemsToRemove = new ArrayList<>();
            for (T temporaryItem : temporaryItems) {
                boolean preAccessible = field.isAccessible();
                field.setAccessible(true);
                final Object objectValue = field.get(temporaryItem);
                field.setAccessible(preAccessible);
                final Collection<?> collectionObject = (Collection<?>) objectValue;
                boolean containsAny = false;
                for (Object givenValue : values) {
                    if (collectionObject.contains(givenValue)) {
                        containsAny = true;
                        break;
                    }
                }
                if (!containsAny) {
                    itemsToRemove.add(temporaryItem);
                }
            }

            for (T item : itemsToRemove) {
                temporaryItems.remove(item);
            }

        } catch (IllegalAccessException | ClassCastException e) {
            throw new InvalidFieldException(e);
        }

        return this;
    }

    /**
     * Orders items based on a field's value in a specific direction.
     * @param fieldName The field name.
     * @param direction The direction of ordering.
     * @return Returns a filterable.
     */
    public ObjectisFilterable<T> orderBy(String fieldName, OrderDirection direction) {
        final Field field = getField(fieldName);
        if (field == null) {
            throw new InvalidFieldException("The field '" + fieldName + "' does not exist in class '" + aClass.getSimpleName() + "'.");
        }

        temporaryItems.sort((o1, o2) -> {
            try {
                boolean preAccessible = field.isAccessible();
                field.setAccessible(true);
                final Object objectValue1 = field.get(o1);
                final Object objectValue2 = field.get(o2);
                Class<?> type = field.getType();
                type = fixUnboxedType(type);

                field.setAccessible(preAccessible);

                final Comparable<Object> castedObject1 = (Comparable<Object>) type.cast(objectValue1);
                final Comparable<Object> castedObject2 = (Comparable<Object>) type.cast(objectValue2);

                if (direction == OrderDirection.ASCENDING) {
                    return castedObject1.compareTo(castedObject2);
                }
                else {
                    return castedObject2.compareTo(castedObject1);
                }

            } catch (IllegalAccessException | ClassCastException e) {
                throw new InvalidFieldException(e);
            }
        });

        return this;
    }

    /**
     * Limits the results of the query to the limit provided.
     * @param limit The limit.
     * @return Returns a filterable.
     */
    public ObjectisFilterable<T> limit(int limit) {
        if (limit > 0 && temporaryItems.size() >= limit) {
            temporaryItems = new Vector<>(temporaryItems.subList(0, limit));
        }
        return this;
    }

    /**
     * Offsets the result of the query based on the offset provided.
     * @param offset The offset.
     * @return Returns a filterable.
     */
    public ObjectisFilterable<T> offset(int offset) {
        if (offset >= 0 && offset < temporaryItems.size()) {
            temporaryItems = new Vector<>(temporaryItems.subList(offset, temporaryItems.size()));
        }
        return this;
    }

    /**
     * Fetches the result.
     * @return Returns a collection of objects.
     */
    public ObjectisQueryResult<T> fetch() {
        try {
            if (temporaryItems.size() > 0) {
                final String lastElementID = Reflector.getIDField(temporaryItems.lastElement());
                return new ObjectisQueryResult<>(temporaryItems, lastElementID);
            }
            return new ObjectisQueryResult<>(temporaryItems, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new OperationFailedException(e);
        }
    }

    /**
     * Searches the class fields for a particular field by name.
     * @param fieldName The field name.
     * @return Returns the field, or null if the field was not found.
     */
    private Field getField(String fieldName) {
        for (Field classField : classFields) {
            if (classField.getName().equals(fieldName)) {
                return classField;
            }
        }
        return null;
    }

    private Class<?> fixUnboxedType(Class<?> type) {
        if (type == int.class) {
            return Integer.class;
        }
        else if (type == double.class) {
            return Double.class;
        }
        else if (type == float.class) {
            return Float.class;
        }
        else if (type == long.class) {
            return Long.class;
        }
        else if (type == short.class) {
            return Short.class;
        }
        else if (type == byte.class) {
            return Byte.class;
        }
        else if (type == char.class) {
            return Character.class;
        }
        else if (type == boolean.class) {
            return Boolean.class;
        }
        else {
            return type;
        }
    }

}
