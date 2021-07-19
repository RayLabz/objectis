package com.raylabz.objectis.concurrency;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class FilterCallable<T> implements Callable<List<T>> {

    private final Class<T> aClass;
    private final FilterCallableProcessor<T> callableProcessor;
    private final List<T> items;
    private final ArrayRange range;
    private final Field field;

    public FilterCallable(Class<T> aClass, Field field, List<T> items, ArrayRange range, FilterCallableProcessor<T> callableProcessor) {
        this.aClass = aClass;
        this.field = field;
        this.callableProcessor = callableProcessor;
        this.items = items;
        this.range = range;
    }

    @Override
    public List<T> call() throws Exception {
        ArrayList<T> itemsToRemove = new ArrayList<>();
        for (int i = range.getStartingItemIndex(); i < range.getEndingItemIndex(); i++) {
            if (callableProcessor.exclude(items.get(i))) {
                itemsToRemove.add(items.get(i));
            }
        }
        return itemsToRemove;
    }

}
