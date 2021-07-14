import com.raylabz.objectis.Objectis;
import com.raylabz.objectis.exception.ClassRegistrationException;

import java.util.ArrayList;
import java.util.UUID;

public class Benchmark {

    public static void main(String[] args) throws ClassRegistrationException {
        Objectis.register(Person.class);
        Objectis.init();
        Objectis.flush();

        final ArrayList<String> ids = createBenchmark();
        getBenchmark(ids);
        listBenchmark();
        existsBenchmark(ids);
        deleteBenchmark(ids);

    }

    public static ArrayList<String> createBenchmark() {
        ArrayList<Long> times = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>();

        int numOfTests = 100;
        for (int i = 0; i < numOfTests; i++) {
            final String id = UUID.randomUUID().toString();
            Person p = new Person(id, i + 20, "John " + i, "Smith " + i);
            p.addFriend("a");
            p.addFriend("b");
            p.addFriend("c");

            final long l = System.currentTimeMillis();
            Objectis.set(p);
            times.add(System.currentTimeMillis() - l);
            ids.add(id);

        }

        long sum = 0;
        for (Long l : times) {
            sum += l;
        }

        double average = sum / (double) times.size();
        System.out.println("Average create() time: " + String.format("%.2f", average) + " ms");

        return ids;
    }

    public static void getBenchmark(ArrayList<String> ids) {
        ArrayList<Long> times = new ArrayList<>();

        for (String id : ids) {
            long t = System.currentTimeMillis();
            Objectis.get(Person.class, id);
            times.add(System.currentTimeMillis() - t);
        }

        long sum = 0;
        for (Long l : times) {
            sum += l;
        }

        double average = sum / (double) times.size();
        System.out.println("Average get() time: " + String.format("%.2f", average) + " ms");
    }

    public static void listBenchmark() {
        ArrayList<Long> times = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            final long t = System.currentTimeMillis();
            Objectis.list(Person.class);
            times.add(System.currentTimeMillis() - t);
        }

        long sum = 0;
        for (Long l : times) {
            sum += l;
        }

        double average = sum / (double) times.size();
        System.out.println("Average list() time: " + String.format("%.2f", average) + " ms");
    }

    public static void existsBenchmark(ArrayList<String> ids) {
        ArrayList<Long> times = new ArrayList<>();
        for (String id : ids) {
            final long t = System.currentTimeMillis();
            Objectis.exists(Person.class, id);
            times.add(System.currentTimeMillis() - t);
        }

        long sum = 0;
        for (Long l : times) {
            sum += l;
        }

        double average = sum / (double) times.size();
        System.out.println("Average exists() time: " + String.format("%.2f", average) + " ms");
    }

    public static void deleteBenchmark(ArrayList<String> ids) {
        ArrayList<Long> times = new ArrayList<>();
        for (String id : ids) {
            final long t = System.currentTimeMillis();
            Objectis.delete(Person.class, id);
            times.add(System.currentTimeMillis() - t);
        }

        long sum = 0;
        for (Long l : times) {
            sum += l;
        }

        double average = sum / (double) times.size();
        System.out.println("Average delete() time: " + String.format("%.2f", average) + " ms");
    }

}
