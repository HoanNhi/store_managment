package controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import adapter.DataAdapterMongo;
import structure.Order;
import structure.OrderItem;
import structure.Product;
import structure.Customer;
import view.BuyerView;
import view.CustomerView;
import adapter.DataAdapter;

public class CheckoutController implements ActionListener {
    private BuyerView view;
    private DataAdapter dataAdapter; // to save and load product
    private DataAdapterMongo dataAdapterMongo;
    private Order order = null;
    private CustomerView customerView; // Add customer dialog

    public CheckoutController(BuyerView view, DataAdapter dataAdapter, DataAdapterMongo dataAdapterMongo) {
        this.dataAdapter = dataAdapter;
        this.dataAdapterMongo = dataAdapterMongo;
        this.view = view;
        this.customerView = new CustomerView(view, dataAdapterMongo);

        view.getBtnAdd().addActionListener(this);
        view.getBtnPay().addActionListener(this);

        order = new Order();

    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getBtnAdd())
            addProduct();
        else
        if (e.getSource() == view.getBtnPay())
            makeOrder();
    }

    private void makeOrder() {
        if (this.order.getLines().size() == 0) {
            JOptionPane.showMessageDialog(null, "Please choose at least one product");
            return;
        }

        customerView.setVisible(true);

        Customer customer = customerView.getCustomer();
        if (customer != null) { //Check if customer was returned
            this.order.setCustomerID(customer.getCustomerID()); // Set CustomerID in the order

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
            this.order.setAddress(customerView.getAddress());
            this.order.setShipperName(customerView.getShipperName());
            if(dataAdapterMongo.saveOrder(this.order)){
                JOptionPane.showMessageDialog(null, "Order added successfully");
                view.clearOrder();
                order = new Order();
            }
            else{
                JOptionPane.showMessageDialog(null, "Order failed!");
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "Please choose a customer");
            return;
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
        order.addLine(line);
        order.setTotalCost(order.getTotalCost() + line.getCost());


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

}