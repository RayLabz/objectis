import com.raylabz.objectis.Objectis;
import com.raylabz.objectis.PathMaker;
import com.raylabz.objectis.Serializer;
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
//        Objectis.init("192.168.10.1", 6379, 30, true);
        Objectis.register(Person.class);
        Objectis.flush();

        Person aPerson = new Person("myID", 100, "N", "K");
        Objectis.create(aPerson);

        for (int i = 0; i < 1000; i++) {
            String uuid = UUID.randomUUID().toString();
            Person x = new Person(uuid, i + 20, "N" + i, "K" + i);
            x.addFriend("friend " + i);
            x.addFriend("friendz " + i);
            Objectis.create(x);
        }

        final long t = System.currentTimeMillis();
        final Collection<Person> items = Objectis.filter(Person.class)
                .whereEqualTo("name", "N2")
                .fetch();
        System.out.println("Filter: " + (System.currentTimeMillis() - t) + "ms");

    }


}
