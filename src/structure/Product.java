package structure;

public class Product {
    private int productID;
    private String name;
    private String category;
    private int supplierID;
    private double price;
    private String description;
    private int Quantity;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public String getDescription() { return description; }

    public String setDescription(String description) { this.description = description; return description; }

    public int getQuantity() { return Quantity; }

    public void setQuantity(int quantity) { Quantity = quantity; }
}
