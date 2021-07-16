//import com.raylabz.objectis.Objectis;
//import com.raylabz.objectis.PathMaker;
//import com.raylabz.objectis.Serializer;
//import com.raylabz.objectis.exception.ClassRegistrationException;
//import com.raylabz.objectis.pubsub.OperationType;
//import redis.clients.jedis.BinaryJedisPubSub;
//
//public class AdHocSub {
//
//    public static void main(String[] args) throws ClassRegistrationException {
//        Objectis.init();
//        Objectis.register(Person.class);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Objectis.getJedis().subscribe(new BinaryJedisPubSub() {
//                    @Override
//                    public void onMessage(byte[] channel, byte[] message) {
//                        final Person person = Serializer.deserializeObject(message, Person.class);
//                        System.out.println(person);
//                    }
//                }, PathMaker.getPublishPath(Person.class, "myID", OperationType.CREATE));
//            }
//        }).start();
//
//
//        new Thread(() -> {
//            Objectis.getJedis().subscribe(new BinaryJedisPubSub() {
//                @Override
//                public void onMessage(byte[] channel, byte[] message) {
//                    final Person person = Serializer.deserializeObject(message, Person.class);
//                    System.out.println(person);
//                }
//            }, PathMaker.getPublishPath(Person.class, "myID", OperationType.UPDATE));
//        }).start();
//
//
//
//    }
//
//}
