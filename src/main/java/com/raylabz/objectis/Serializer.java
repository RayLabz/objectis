package com.raylabz.objectis;

import com.sun.xml.internal.ws.encoding.soap.DeserializationException;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class Serializer {

    public static byte[] serializeKey(String key) {
        return key.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] serializeObject(Object object) throws SerializationException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    public static <T> T deserializeObject(byte[] bytes, Class<T> aClass) throws DeserializationException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            Object o = in.readObject();
            return aClass.cast(o);
        } catch (IOException | ClassNotFoundException e) {
            throw new DeserializationException(e);
        }
    }

}
