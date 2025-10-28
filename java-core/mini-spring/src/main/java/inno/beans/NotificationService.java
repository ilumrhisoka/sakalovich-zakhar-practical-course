package inno.beans;

import minispring.annotations.Component;

@Component
public class NotificationService {
    public void sendNotification(String message) {
        System.out.println("NotificationService.sendNotification: " + message);
    }
}
