package inno.intern;

public class OrderItem {
    public OrderItem(String productName, double price, int quantity, Category category) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    private String productName;
    private int quantity;
    private double price;
    private Category category;


    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
