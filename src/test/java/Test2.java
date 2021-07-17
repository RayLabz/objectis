import com.raylabz.objectis.Objectis;
import com.raylabz.objectis.exception.ClassRegistrationException;

import java.util.ArrayList;
import java.util.UUID;

public class Test2 {

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


        final ArrayList<Person> fetch = Objectis.filter(Person.class).offset(11).fetch();
        for (Person person : fetch) {
            System.out.println(person);
        }


    }

}
