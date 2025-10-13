package inno.intern;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Factory factory = new Factory();

        Thread factoryThread = new Thread(factory);
        Faction world = new Faction("World", factory);
        Faction wednesday = new Faction("Wednesday", factory);

        Thread worldThread = new Thread(world);
        Thread wednesdayThread = new Thread(wednesday);

        factoryThread.start();
        worldThread.start();
        wednesdayThread.start();

        factoryThread.join();
        worldThread.join();
        wednesdayThread.join();

        System.out.println("                ");
        System.out.println("World robots: " + world.getRobotsBuilt());
        System.out.println("Wednesday robots: " + wednesday.getRobotsBuilt());
        System.out.println(world.getRobotsBuilt() > wednesday.getRobotsBuilt()
                ? "World wins"
                : "Wednesday wins");
    }
}
