import com.raylabz.objectis.Objectis;
import com.raylabz.objectis.exception.ClassRegistrationException;

import java.util.UUID;

public class Test {

    public static void main(String[] args) throws ClassRegistrationException {
        Objectis.init();
        Objectis.register(Person.class);

        String uuid = UUID.randomUUID().toString();
        Person x = new Person(uuid, 20, "N", "K");
        x.addFriend("a");
        x.addFriend("b");
        x.addFriend("c");

        Objectis.create(x);
        final boolean exists = Objectis.exists(Person.class, uuid);
        System.out.println(exists);

        Objectis.delete(Person.class, uuid);

        final boolean exists1 = Objectis.exists(Person.class, uuid);
        System.out.println(exists1);

    }


}
