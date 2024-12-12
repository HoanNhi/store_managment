package controller;

import adapter.*;
import main.Application;
import structure.Shipper;
import structure.Order;
import structure.OrderItem;
import structure.Product;
import view.CustomerBuyerProduct;
import view.CustomerBuyerView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class CustomerBuyerController implements ActionListener {
    private CustomerBuyerView view;
    private DataAdapterInterface dataAdapter; // to save and load product
    private DataAdapterInterface dataAdapterMongo;
    private Order order = null;
    private CustomerBuyerProduct customerBuyerView;  // Add ProductDetailView field


    public CustomerBuyerController(CustomerBuyerView view, DataAdapterInterface dataAdapter, DataAdapterInterface dataAdapterMongo) {
        this.dataAdapter = dataAdapter;
        this.dataAdapterMongo = dataAdapterMongo;
        this.view = view;
//        this.customerView = new CustomerView(view, dataAdapterMongo);

        view.getBtnAdd().addActionListener(this);
        view.getBtnPay().addActionListener(this);
        view.getBtnLoadProducts().addActionListener(this);

        order = new Order();

    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getBtnAdd())
            addProduct();
        else if (e.getSource() == view.getBtnPay())
            makeOrder();
        else if (e.getSource() == view.getBtnLoadProducts()) {
            loadAndShowProducts(); // New method to load and show products
        }
    }

    private void makeOrder() {
        if (this.order.getLines().size() == 0) {
            JOptionPane.showMessageDialog(null, "Please choose at least one product");
            return;
        }

        // 1. Input Dialog for Address
        String address = JOptionPane.showInputDialog(view, "Enter shipping address:", "Shipping Address", JOptionPane.QUESTION_MESSAGE);
        if (address == null || address.trim().isEmpty()) { // Handle cancel or empty input
            return;
        }

        // 2. Option Dialog (or Combo Box) for Shipper Selection
        List<Shipper> shippers = dataAdapterMongo.loadAllShippers();
        if (shippers == null || shippers.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No shippers available. Cannot create order.");
            return;
        }

        String[] shipperNames = shippers.stream().map(Shipper::getShipperName).toArray(String[]::new);

        String selectedShipper = (String) JOptionPane.showInputDialog(
                view,
                "Select a shipper:",
                "Shipper Selection",
                JOptionPane.QUESTION_MESSAGE,
                null, // No icon
                shipperNames, // Array of shipper names
                shipperNames[0] // Default selection (first shipper)
        );

        if (selectedShipper == null) { // Handle cancel
            return;
        }

        this.order.setCustomerID(Application.getInstance().getCurrentUser().getUserID()); // Set CustomerID in the order
        for (OrderItem line : this.order.getLines()) {
            Product product = dataAdapter.loadProduct(line.getProductID());

            if (product != null) { //Check if product exists before updating quantity
                int updatedQuantity = product.getQuantity() - line.getQuantity();
                if (updatedQuantity >= 0) { //Check for negative quantities
                    product.setQuantity(updatedQuantity); // Update quantity
                    dataAdapter.saveProduct(product);    // Save product
                } else {
                    JOptionPane.showMessageDialog(null, "Not enough product in stock. ");
                    return;
                }
            }
        }

        this.order.setDate(Timestamp.valueOf(LocalDateTime.now()));
        this.order.setAddress(address);
        this.order.setShipperName(selectedShipper);
        if(dataAdapterMongo.saveOrder(this.order)){
            JOptionPane.showMessageDialog(null, "Order added successfully");
            view.clearOrder();
            order = new Order();
        }
        else{
            JOptionPane.showMessageDialog(null, "Order failed!");
        }

    }

    private void addProduct() {
        String id = JOptionPane.showInputDialog("Enter ProductID: ");
        Product product = dataAdapter.loadProduct(Integer.parseInt(id));
        if (product == null) {
            JOptionPane.showMessageDialog(null, "This product does not exist!");
            return;
        }

        int quantity = Integer.parseInt(JOptionPane.showInputDialog(null,"Enter quantity: "));

        if (quantity < 0 || quantity > product.getQuantity()) {
            JOptionPane.showMessageDialog(null, "This quantity is not valid!");
            return;
        }

        OrderItem line = new OrderItem();
        line.setProductID(product.getProductID());
        line.setProductName(product.getName());
        line.setQuantity(quantity);
        line.setUnitCost(product.getPrice());
        line.setCost(quantity * product.getPrice());
        this.order.addLine(line);
        this.order.setTotalCost(this.order.getTotalCost() + line.getCost());


        Object[] row = new Object[5];
        row[0] = line.getProductID();
        row[1] = product.getName();
        row[2] = product.getPrice();
        row[3] = line.getQuantity();
        row[4] = line.getCost();

        this.view.addRow(row);
        this.view.getLabTotal().setText("Total: $" + order.getTotalCost());
        this.view.invalidate();
    }

    private void loadAndShowProducts() {
        List<Product> products = dataAdapter.loadAllProducts(); // Fetch products
        customerBuyerView = new CustomerBuyerProduct();
        customerBuyerView.showProduct(products);               // Set fetched data
        customerBuyerView.setVisible(true);                   // Show product detail view
    }

}