package inno.beans;

import minispring.annotations.Autowired;
import minispring.annotations.Component;
import minispring.lifecycle.InitializingBean;

@Component
public class UserService implements InitializingBean {

    @Autowired
    private NotificationService notificationSerivce;

    private String status;

    public void processUser(String user){
        System.out.println("UserService.processUser(): " + user);
        notificationSerivce.sendNotification("User processed: " + user);
    }

    public String getStatus() {
        return status;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.status = "Initialized";
        System.out.println("UserService.status: " + this.status);
    }
}
