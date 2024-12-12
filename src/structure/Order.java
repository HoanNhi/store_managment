package structure;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;


public class Order {
    private int orderID;
    private int customerID;
    private Timestamp date;
//    private int EmployeeID;
    private double totalCost = 0;
    private String address;
    private String shipperName;

    private List<OrderItem> lines = new ArrayList<>();

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

//    public void setEmployeeID(int employeeID) {
//        EmployeeID = employeeID;
//    }
//
//    public int getEmployeeID() { return this.EmployeeID; }

    public void setLines(List<OrderItem> lines) { this.lines = lines; }

    public void addLine(OrderItem line) {
        lines.add(line);
    }

    public void removeLine(OrderItem line) {
        lines.remove(line);
    }

    public List<OrderItem> getLines() {
        return lines == null ? new ArrayList<>() : lines;
    }

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
