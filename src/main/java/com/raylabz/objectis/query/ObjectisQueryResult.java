package com.raylabz.objectis.query;

import java.util.List;

public class ObjectisQueryResult<T> {

    private final List<T> items;
    private final String lastDocumentID;

    public ObjectisQueryResult(List<T> items, String lastDocumentID) {
        this.items = items;
        this.lastDocumentID = lastDocumentID;
    }

    public List<T> getItems() {
        return items;
    }

    public String getLastDocumentID() {
        return lastDocumentID;
    }

    public boolean hasItems() {
        return (!items.isEmpty());
    }

}
