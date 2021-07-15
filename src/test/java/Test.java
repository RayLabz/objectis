import com.raylabz.objectis.Objectis;
import com.raylabz.objectis.exception.ClassRegistrationException;
import com.raylabz.objectis.query.OrderDirection;

import java.util.Collection;
import java.util.Scanner;
import java.util.UUID;

public class Test {

    public static void main(String[] args) throws ClassRegistrationException {
        Objectis.init();
        Objectis.register(Person.class);
        Objectis.flush();

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


//        final boolean exists = Objectis.exists(Person.class, uuid);
//        System.out.println(exists);
//
//        Objectis.delete(Person.class, uuid);
//
//        final boolean exists1 = Objectis.exists(Person.class, uuid);
//        System.out.println(exists1);

    }


}
