import com.raylabz.objectis.Objectis;
import com.raylabz.objectis.PathMaker;
import com.raylabz.objectis.Serializer;
import com.raylabz.objectis.exception.ClassRegistrationException;
import com.raylabz.objectis.query.OrderDirection;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.UUID;

public class Test {

    public static void main(String[] args) throws ClassRegistrationException, InterruptedException {
        JedisPool jedisPool = new JedisPool();
        Objectis.init(jedisPool);
        Objectis.register(Person.class);
        Objectis.flush();

        ArrayList<Person> personArrayList = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            Person p = new Person(UUID.randomUUID().toString(), 10, "aaa", "bbb");
            personArrayList.add(p);
        }

        Objectis.useMultithreading(true);

        long s = System.currentTimeMillis();
        Objectis.createAll(personArrayList);
        long f = System.currentTimeMillis();

        System.out.println(f - s);

    }


}
