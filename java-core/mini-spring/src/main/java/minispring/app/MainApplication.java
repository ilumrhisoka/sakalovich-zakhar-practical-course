package minispring.app;

import inno.beans.PrototypeCounter;
import inno.beans.UserService;
import minispring.core.MiniApplicationContext;

public class MainApplication {
    public static void main(String[] args) {
        MiniApplicationContext context = new MiniApplicationContext("inno.beans");

        UserService userService1 = context.getBean(UserService.class);
        userService1.processUser("John");
        System.out.println("UserService1.status: " + userService1.getStatus());

        UserService userService2 = context.getBean(UserService.class);

        System.out.println("UserService1 == UserService2:" + (userService1 == userService2));

        PrototypeCounter prototypeCounter1 = context.getBean(PrototypeCounter.class);
        PrototypeCounter prototypeCounter2 = context.getBean(PrototypeCounter.class);
        PrototypeCounter prototypeCounter3 = context.getBean(PrototypeCounter.class);

        System.out.println("Prototype1.id = " + prototypeCounter1.getInstaceId());
        System.out.println("Prototype2.id = " + prototypeCounter2.getInstaceId());
        System.out.println("Prototype3.id = " + prototypeCounter3.getInstaceId());

        System.out.println("Prototype1 == Prototype2:" + (prototypeCounter1 == prototypeCounter2));
    }
}
