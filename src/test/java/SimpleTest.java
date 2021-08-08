import com.raylabz.objectis.Objectis;
import com.raylabz.objectis.exception.ClassRegistrationException;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SimpleTest {

    public static void main(String[] args) throws ClassRegistrationException {
        JedisPool jedisPool = new JedisPool();
        Objectis.init(jedisPool);
        Objectis.register(Person.class);
        Objectis.flush();

        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final int name = i;
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        Person p = new Person(UUID.randomUUID().toString(), 10 + i, UUID.randomUUID().toString().substring(3), UUID.randomUUID().toString().substring(3));
                        Person p2 = new Person(UUID.randomUUID().toString(), 10 + i, UUID.randomUUID().toString().substring(3), UUID.randomUUID().toString().substring(3));
                        Objectis.collection(Person.class, "myCollection").add(p);
                        Objectis.create(p);
                        Objectis.get(Person.class, "X");
                        Objectis.create(p2);
                        Objectis.list(Person.class);
                        System.out.println("Thread " + name + " created person " + i);
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
//                    final List<Person> list = Objectis.list(Person.class);
//                    System.out.println(list.size());
                }
            }));
        }

        for (Thread thread : threads) {
            thread.start();
        }


    }

}
