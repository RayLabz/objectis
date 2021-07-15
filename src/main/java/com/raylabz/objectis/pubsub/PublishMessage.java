package com.raylabz.objectis.pubsub;

import com.raylabz.objectis.Serializer;

import java.io.Serializable;

public class PublishMessage implements Serializable {

    private final Class<?> aClass;
    private final String id;
    private final OperationType operationType;
    private final Object object;

    public PublishMessage(Class<?> aClass, String id, OperationType operationType) {
        this.aClass = aClass;
        this.id = id;
        this.operationType = operationType;
        this.object = null;
    }

    public PublishMessage(Class<?> aClass, String id, OperationType operationType, Object object) {
        this.aClass = aClass;
        this.id = id;
        this.operationType = operationType;
        this.object = object;
    }

    public Class<?> getaClass() {
        return aClass;
    }

    public String getId() {
        return id;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public Object getObject() {
        return object;
    }

    public byte[] toBytes() {
        return Serializer.serializeObject(this);
    }

    public static PublishMessage fromBytes(byte[] bytes) {
        return Serializer.deserializeObject(bytes, PublishMessage.class);
    }

}
