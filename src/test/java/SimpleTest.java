import com.raylabz.objectis.Objectis;
import com.raylabz.objectis.exception.ClassRegistrationException;

import java.util.List;
import java.util.UUID;

public class SimpleTest {

    public static void main(String[] args) throws ClassRegistrationException {
        Objectis.init();
        Objectis.register(Person.class);
        Objectis.flush();
//        Person p = new Person(UUID.randomUUID().toString(), 23, "NK1", "X");
//        Objectis.create(p);
        final List<Person> list = Objectis.filter(Person.class)
                .whereEqualTo("name", "NK1")
                .limit(1)
                .fetch().getItems();
        System.out.println(list);
    }

}
