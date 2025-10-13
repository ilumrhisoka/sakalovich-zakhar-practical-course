package inno.intern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MetricCalculatorTest {

    private MetricCalculator calculator;

    @BeforeEach
    void setUp() {
        Customer c1 = new Customer("C001", "Alice", "a@mail.com", LocalDateTime.now(), 30, "Moscow");
        Customer c2 = new Customer("C002", "Bob", "b@mail.com", LocalDateTime.now(), 25, "Saint Petersburg");
        Customer c3 = new Customer("C003", "Charlie", "c@mail.com", LocalDateTime.now(), 40, "Moscow");
        Customer c4 = new Customer("C004", "Dave", "d@mail.com", LocalDateTime.now(), 35, "Kazan");

        OrderItem item1 = new OrderItem("Laptop", 1000.0, 1, Category.ELECTRONICS);
        OrderItem item2 = new OrderItem("T-Shirt", 20.0, 5, Category.CLOTHING);
        OrderItem item3 = new OrderItem("Book", 15.0, 2, Category.BOOKS); // 30.0
        OrderItem item4 = new OrderItem("Mouse", 25.0, 10, Category.ELECTRONICS); // 250.0

        List<Order> testOrders = Arrays.asList(
                new Order("O001", LocalDateTime.now(), c1, List.of(item1), OrderStatus.DELIVERED),

                new Order("O002", LocalDateTime.now(), c2, List.of(item2, item3), OrderStatus.DELIVERED),

                new Order("O003", LocalDateTime.now(), c1, List.of(item4), OrderStatus.CANCELLED),

                new Order("O004", LocalDateTime.now(), c4, List.of(item1), OrderStatus.PROCESSING),

                new Order("O005", LocalDateTime.now(), c3, List.of(item2), OrderStatus.DELIVERED),
                new Order("O006", LocalDateTime.now(), c3, List.of(item2), OrderStatus.DELIVERED),
                new Order("O007", LocalDateTime.now(), c3, List.of(item2), OrderStatus.DELIVERED),
                new Order("O008", LocalDateTime.now(), c3, List.of(item2), OrderStatus.DELIVERED),
                new Order("O009", LocalDateTime.now(), c3, List.of(item2), OrderStatus.DELIVERED),
                new Order("O010", LocalDateTime.now(), c3, List.of(item2), OrderStatus.DELIVERED)
        );

        calculator = new MetricCalculator(testOrders);
    }

    @Test
    void getUniqueOrderCities_ShouldReturnCorrectSet() {
        Set<String> expectedCities = Set.of("Moscow", "Saint Petersburg", "Kazan");
        Set<String> actualCities = calculator.getUniqueOrderCities();

        assertEquals(expectedCities.size(), actualCities.size());
        assertTrue(actualCities.containsAll(expectedCities));
    }

    @Test
    void getTotalIncomeForCompletedOrders_ShouldCalculateCorrectSum() {
        double expectedIncome = 1730.0;

        assertEquals(expectedIncome, calculator.getTotalIncomeForCompletedOrders(), 0.01);
    }

    @Test
    void getMostPopularProductBySales_ShouldReturnCorrectProduct() {
        String expectedProduct = "T-Shirt";

        assertEquals(expectedProduct, calculator.getMostPopularProductBySales());
    }

    @Test
    void getAverageCheckForDeliveredOrders_ShouldCalculateCorrectAverage() {
        double expectedAverage = 1730.0 / 8.0;

        assertEquals(expectedAverage, calculator.getAverageCheckForDeliveredOrders(), 0.01);
    }

    @Test
    void getCustomersWithMoreThanFiveOrders_ShouldReturnCorrectCustomer() {
        List<Customer> customers = calculator.getCustomersWithMoreThanFiveOrders();

        assertEquals(1, customers.size());
        assertEquals("C003", customers.get(0).getCustomerId());
    }

    @Test
    void testWithEmptyList() {
        MetricCalculator emptyCalculator = new MetricCalculator(List.of());

        assertTrue(emptyCalculator.getUniqueOrderCities().isEmpty());
        assertEquals(0.0, emptyCalculator.getTotalIncomeForCompletedOrders(), 0.0);
        assertEquals("N/A", emptyCalculator.getMostPopularProductBySales());
        assertEquals(0.0, emptyCalculator.getAverageCheckForDeliveredOrders(), 0.0);
        assertTrue(emptyCalculator.getCustomersWithMoreThanFiveOrders().isEmpty());
    }
}