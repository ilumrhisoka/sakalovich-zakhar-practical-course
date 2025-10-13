package inno.intern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Faction implements Runnable {
    private final String name;
    private final Factory factory;
    private final List<Part> myParts = new ArrayList<>();
    private int robotsBuilt = 0;

    public Faction(String name, Factory factory) {
        this.name = name;
        this.factory = factory;
    }

    @Override
    public void run() {
        for (int day = 1; day <= 100; day++) {
            List<Part> parts = factory.getParts(5);
            myParts.addAll(parts);

            buildRobots();

            System.out.println(name + " got " + parts.size() + " parts. Robots: " + robotsBuilt);

            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}
        }
    }

    private void buildRobots() {
        while (canBuildRobot()) {
            removePart(Part.HEAD, 1);
            removePart(Part.TORSO, 1);
            removePart(Part.HAND, 2);
            removePart(Part.FEET, 2);
            robotsBuilt++;
        }
    }

    private boolean canBuildRobot() {
        return countPart(Part.HEAD) >= 1 &&
                countPart(Part.TORSO) >= 1 &&
                countPart(Part.HAND) >= 2 &&
                countPart(Part.FEET) >= 2;
    }

    private int countPart(Part part) {
        return (int) myParts.stream().filter(p -> p == part).count();
    }

    private void removePart(Part part, int count) {
        int removed = 0;
        Iterator<Part> it = myParts.iterator();
        while (it.hasNext() && removed < count) {
            if (it.next() == part) {
                it.remove();
                removed++;
            }
        }
    }

    public int getRobotsBuilt() {
        return robotsBuilt;
    }
}

