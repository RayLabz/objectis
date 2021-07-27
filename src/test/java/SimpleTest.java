import com.raylabz.objectis.Objectis;
import com.raylabz.objectis.exception.ClassRegistrationException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SimpleTest {

    public static void main(String[] args) throws ClassRegistrationException {
        Objectis.init();
        Objectis.register(Person.class);
        Objectis.flush();

        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            final int name = i;
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 100; i++) {
                        Person p = new Person(UUID.randomUUID().toString(), 10 + i, UUID.randomUUID().toString().substring(3), UUID.randomUUID().toString().substring(3));
                        Objectis.create(p);
//                        System.out.println("Thread " + name + " created person " + i);
                    }
                    final List<Person> list = Objectis.list(Person.class);
                    System.out.println(list);
                }
            }));
        }

        for (Thread thread : threads) {
            thread.start();
        }


    }

}
