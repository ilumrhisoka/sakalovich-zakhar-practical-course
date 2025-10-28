package inno.beans;

import minispring.annotations.Component;
import minispring.annotations.Scope;

@Component
@Scope("prototype")
public class PrototypeCounter {
    private static int counter = 0;
    private final int instaceId;

    public PrototypeCounter() {
        counter++;
        this.instaceId = counter;
    }

    public int getInstaceId() {
        return instaceId;
    }
}
