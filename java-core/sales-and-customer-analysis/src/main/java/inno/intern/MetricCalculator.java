package inno.intern;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MetricCalculator {

    private final List<Order> orders;

    public MetricCalculator(List<Order> orders) {
        this.orders = orders;
    }

    public Set<String> getUniqueOrderCities() {
        return orders.stream()
                .filter(order -> order.getCustomer() != null)
                .map(order -> order.getCustomer().getCity())
                .collect(Collectors.toSet());
    }

    public double getTotalIncomeForCompletedOrders() {
        return orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(order -> order.getItems().stream()
                        .mapToDouble(item -> item.getPrice() * item.getQuantity())
                        .sum())
                .sum();
    }

    public String getMostPopularProductBySales() {
        return orders.stream()
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(
                        OrderItem::getProductName,
                        Collectors.summingInt(OrderItem::getQuantity)
                ))
                .entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    public double getAverageCheckForDeliveredOrders() {
        return orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(order -> order.getItems().stream()
                        .mapToDouble(item -> item.getPrice() * item.getQuantity())
                        .sum())
                .average()
                .orElse(0.0);
    }

    public List<Customer> getCustomersWithMoreThanFiveOrders() {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        Order::getCustomer,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}