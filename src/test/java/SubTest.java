//import com.raylabz.objectis.Objectis;
//import com.raylabz.objectis.PathMaker;
//import com.raylabz.objectis.Serializer;
//import com.raylabz.objectis.exception.ClassRegistrationException;
//import com.raylabz.objectis.pubsub.OperationType;
//import com.raylabz.objectis.pubsub.Subscriber;
//import redis.clients.jedis.BinaryJedisPubSub;
//
//public class SubTest {
//
//    public static void main(String[] args) throws ClassRegistrationException, InterruptedException {
//        Objectis.init();
//        Objectis.register(Person.class);
//
////        final Subscriber createSub = new Subscriber(Person.class, "myID", OperationType.CREATE, new BinaryJedisPubSub() {
////            @Override
////            public void onMessage(byte[] channel, byte[] message) {
////                final Person person = Serializer.deserializeObject(message, Person.class);
////                System.out.println(person);
////            }
////        });
//
//        final Subscriber updateSub = new Subscriber(Person.class, "myID", OperationType.UPDATE, new BinaryJedisPubSub() {
//            @Override
//            public void onMessage(byte[] channel, byte[] message) {
//                final Person person = Serializer.deserializeObject(message, Person.class);
//                System.out.println(person);
//            }
//        });
//
////        createSub.start();
//        updateSub.start();
//
//        Thread.sleep(2000);
//
//        updateSub.unsubscribe();
//
//
//    }
//
//}
