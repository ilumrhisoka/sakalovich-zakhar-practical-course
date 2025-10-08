package inno.intern;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Factory implements Runnable {
    private final List<Part> storage = new ArrayList<>();
    private final Random random = new Random();
    private final Object lock = new Object();

    public List<Part> getParts(int count) {
        synchronized (lock) {
            int toTake = Math.min(count, storage.size());
            List<Part> taken = new ArrayList<>(storage.subList(0, toTake));
            storage.subList(0, toTake).clear();
            return taken;
        }
    }

    @Override
    public void run() {
        for (int day = 1; day <= 100; day++) {
            synchronized (lock) {
                int partsToProduce = random.nextInt(10) + 1;
                for (int i = 0; i < partsToProduce; i++) {
                    storage.add(Part.values()[random.nextInt(Part.values().length)]);
                }
                System.out.println("Day " + day + ": Factory produced " + partsToProduce + " parts. Total: " + storage.size());
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}
        }
    }
}

