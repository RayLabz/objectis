import com.raylabz.objectis.annotation.Index;
import com.raylabz.objectis.annotation.ObjectisObject;

import java.io.Serializable;
import java.util.ArrayList;

@ObjectisObject
public class Person implements Serializable {

    private String id;
    private int age;
    @Index private String name;
    private String lastname;
    private ArrayList<String> friendNames = new ArrayList<>();

    public Person(String id, int age, String name, String lastname) {
        this.id = id;
        this.age = age;
        this.name = name;
        this.lastname = lastname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void addFriend(String name) {
        friendNames.add(name);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", age=" + age +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", friendNames=" + friendNames +
                '}';
    }

}
