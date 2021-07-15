package com.raylabz.objectis.query;

import com.raylabz.objectis.Objectis;
import com.raylabz.objectis.exception.InvalidFieldException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

public class ObjectisFilterable<T> {

    private final Class<T> aClass;
    private final Vector<T> temporaryItems;
    private final Field[] classFields;

    public ObjectisFilterable(Class<T> aClass) {
        this.aClass = aClass;
        temporaryItems = new Vector<>(Objectis.list(aClass));
        classFields = aClass.getDeclaredFields();
    }

    public ObjectisFilterable<T> whereEqualTo(String fieldName, Object value) {
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

        return this;
    }

    public Collection<T> fetch() {
        return temporaryItems;
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

}
