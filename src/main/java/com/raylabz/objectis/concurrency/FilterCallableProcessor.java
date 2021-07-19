package com.raylabz.objectis.concurrency;

public interface FilterCallableProcessor<T> {

    boolean exclude(T item) throws IllegalAccessException;

}
