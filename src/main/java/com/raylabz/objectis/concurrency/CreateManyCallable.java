package com.raylabz.objectis.concurrency;

import com.raylabz.objectis.Objectis;
import com.raylabz.objectis.PathMaker;
import com.raylabz.objectis.Serializer;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.concurrent.Callable;

public class CreateManyCallable<T> implements Callable<Void> {

    private final List<T> objects;
    private final ArrayRange range;

    public CreateManyCallable(final List<T> objects, final ArrayRange range) {
        this.objects = objects;
        this.range = range;
    }

    @Override
    public Void call() throws Exception {
        Jedis jedis = Objectis.getJedis();
        for (int i = range.getStartingItemIndex(); i < range.getEndingItemIndex(); i++) {
            jedis.set(PathMaker.getObjectPath(objects.get(i)), Serializer.serializeObject(objects.get(i)));
        }
        Objectis.releaseJedis(jedis);
        return null;
    }

}
