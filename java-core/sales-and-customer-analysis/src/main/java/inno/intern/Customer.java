package inno.intern;

import java.time.LocalDateTime;

public class Customer {
    public Customer(String customerId, String name, String email, LocalDateTime registeredAt, int age, String city) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.registeredAt = registeredAt;
        this.age = age;
        this.city = city;
    }

    private String customerId;
    private String name;
    private String email;
    private LocalDateTime registeredAt;
    private int age;
    private String city;


    public String getCustomerId() {
        return customerId;
    }

    public String getCity() {
        return city;
    }

}
