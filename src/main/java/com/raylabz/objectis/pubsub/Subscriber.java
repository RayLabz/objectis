//package com.raylabz.objectis.pubsub;
//
//import com.raylabz.objectis.Objectis;
//import com.raylabz.objectis.PathMaker;
//import redis.clients.jedis.BinaryJedisPubSub;
//import redis.clients.jedis.Jedis;
//
//import java.util.Vector;
//
//public class Subscriber extends Thread {
//
////    static Vector<Subscriber> SUBSCRIBERS = new Vector<>();
//
//    private final Jedis jedisInstance;
//    private final Class<?> objectClass;
//    private final String objectID;
//    private final OperationType operationType;
//    private final BinaryJedisPubSub pubSub;
//
//    private boolean active;
//
//    public Subscriber(Class<?> objectClass, String objectID, OperationType operationType, BinaryJedisPubSub pubSub) {
//        this.objectClass = objectClass;
//        this.objectID = objectID;
//        this.operationType = operationType;
//        this.pubSub = pubSub;
//        this.jedisInstance = new Jedis(Objectis.getJedis().getClient().getHost(), Objectis.getJedis().getClient().getPort());
//    }
//
//    public Class<?> getObjectClass() {
//        return objectClass;
//    }
//
//    public String getObjectID() {
//        return objectID;
//    }
//
//    public OperationType getOperationType() {
//        return operationType;
//    }
//
//    @Override
//    public void run() {
//        jedisInstance.subscribe(pubSub, PathMaker.getPublishPath(objectClass, objectID, operationType));
//    }
//
//    public void unsubscribe() {
//        System.out.println("unsub");
//        pubSub.unsubscribe(PathMaker.getPublishPath(objectClass, objectID, operationType));
//        jedisInstance.close();
//        System.out.println(pubSub.getSubscribedChannels());
//    }
//
//}
