import com.raylabz.objectis.Objectis;
import com.raylabz.objectis.PathMaker;
import com.raylabz.objectis.exception.ClassRegistrationException;
import com.raylabz.objectis.query.OrderDirection;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.JedisPubSub;

import java.util.Collection;
import java.util.Scanner;
import java.util.UUID;

public class Test {

    public static void main(String[] args) throws ClassRegistrationException, InterruptedException {
        Objectis.init("", 6379);
        Objectis.register(Person.class);
        Objectis.flush();

        Person aPerson = new Person("myID", 100, "N", "K");
        Objectis.create(aPerson);

        for (int i = 0; i < 10; i++) {
            String uuid = UUID.randomUUID().toString();
            Person x = new Person(uuid, i + 20, "N" + i, "K" + i);
            x.addFriend("friend " + i);
            x.addFriend("friendz " + i);
            Objectis.create(x);
        }

        final Collection<Person> list = Objectis.list(Person.class);
        for (Person person : list) {
            System.out.println(person);
        }


        final long t = System.currentTimeMillis();
        final Collection<Person> items = Objectis.filter(Person.class)
//                .whereLessThanOrEqualTo("age", 25)
//                .whereEqualTo("name", "N3")
//                .whereArrayContainsAny("friendNames", "friend " + 3, "friendz " + 5)
                .orderBy("name", OrderDirection.DESCENDING)
                .offset(3)
                .limit(5)
                .fetch();
        System.out.println("Filter: " + (System.currentTimeMillis() - t));
//        new Scanner(System.in).nextLine();

        for (Person item : items) {
            System.out.println(item);
        }

        aPerson.setAge(200);
        Objectis.update(aPerson);

//        Objectis.getJedis().subscribe(new BinaryJedisPubSub() {
//            @Override
//            public void onMessage(byte[] channel, byte[] message) {
//                String s = new String(message);
//                System.out.println(s);
//            }
//        }, PathMaker.getClassListPath(Person.class));


//        final boolean exists = Objectis.exists(Person.class, uuid);
//        System.out.println(exists);
//
//        Objectis.delete(Person.class, uuid);
//
//        final boolean exists1 = Objectis.exists(Person.class, uuid);
//        System.out.println(exists1);

    }


}
