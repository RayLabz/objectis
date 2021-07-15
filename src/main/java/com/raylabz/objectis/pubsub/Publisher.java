package com.raylabz.objectis.pubsub;

import com.raylabz.objectis.PathMaker;
import com.raylabz.objectis.Serializer;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;

public class Publisher extends Thread {

    private final Jedis jedis;
    private boolean active = true;

    public Publisher(Jedis jedis) {
        this.jedis = jedis;
    }

    @Override
    public void run() {
        while (active) {

        }
    }

    public void publish(Class<?> aClass, String id, OperationType opType, Object object) {
        byte[] path = PathMaker.getPublishPath(aClass, id, opType);
        if (object != null) {
            jedis.publish(path, Serializer.serializeObject(object));
        }
        else {
            jedis.publish(path, new byte[0]);
        }
    }

}
