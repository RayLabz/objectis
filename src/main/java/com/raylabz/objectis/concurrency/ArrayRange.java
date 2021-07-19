package com.raylabz.objectis.concurrency;

public class ArrayRange {

    private final int startingItemIndex;
    private final int endingItemIndex;

    public ArrayRange(int startingItemIndex, int endingItemIndex) {
        this.startingItemIndex = startingItemIndex;
        this.endingItemIndex = endingItemIndex;
    }

    public int getStartingItemIndex() {
        return startingItemIndex;
    }

    public int getEndingItemIndex() {
        return endingItemIndex;
    }

}
