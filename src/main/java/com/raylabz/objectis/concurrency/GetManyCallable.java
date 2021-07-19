package com.raylabz.objectis.concurrency;

import com.raylabz.objectis.Serializer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class GetManyCallable<T> implements Callable<List<T>> {

    private final Class<T> aClass;
    private final List<byte[]> objectBytes;
    private final ArrayRange range;

    public GetManyCallable(final Class<T> aClass, final List<byte[]> objectBytes, final ArrayRange range) {
        this.aClass = aClass;
        this.objectBytes = objectBytes;
        this.range = range;
    }

    @Override
    public List<T> call() throws Exception {
        ArrayList<T> items = new ArrayList<>();
        for (int i = range.getStartingItemIndex(); i < range.getEndingItemIndex(); i++) {
            final T item = Serializer.deserializeObject(objectBytes.get(i), aClass);
            items.add(item);
        }
        return items;
    }

}
