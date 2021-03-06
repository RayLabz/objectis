//import com.raylabz.objectis.Objectis;
//import com.raylabz.objectis.exception.ClassRegistrationException;
//import com.raylabz.objectis.query.ObjectisCollection;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//public class FilterTest {
//
//    public static void main(String[] args) throws ClassRegistrationException {
//
//        Objectis.init("localhost", 6379, 0, false);
//        Objectis.register(Person.class);
//        Objectis.flush();
//
//        ArrayList<String> ids = new ArrayList<>();
//
//        int NUM_OF_OBJECTS = 10;
//
//        for (int i = 0; i < NUM_OF_OBJECTS; i++) {
//            String uuid = UUID.randomUUID().toString();
////            if (i % 2 == 0) {
//                ids.add(uuid);
////            }
//            Person x = new Person(uuid, i + 20, "N" + i, "K" + i);
//            x.addFriend("friend " + i);
//            x.addFriend("friendz " + i);
//            Objectis.create(x);
//            if (i % 2 == 0) {
//                Objectis.collection(Person.class, "evens").add(x);
//            }
//        }
//
//
//        ArrayList<Long> results = new ArrayList<>();
//        int sum = 0;
//        for (int i = 0; i < 10; i++) {
//            final long t = System.currentTimeMillis();
////            final List<Person> list = Objectis.getMany(Person.class, ids.toArray(new String[0]));
//            final List<Person> items = Objectis.collection(Person.class, "evens").filter().whereEqualTo("name", "N4").fetch().getItems();
//
//            long time = System.currentTimeMillis() - t;
//            System.out.println(items);
//            results.add(time);
//            sum += time;
//        }
//
//        double average = sum / (double) results.size();
//
//        System.out.println("Average time taken for " + NUM_OF_OBJECTS + " items after " + results.size() + " runs: " + average + "ms");
//
//
//
//
//    }
//
//}
