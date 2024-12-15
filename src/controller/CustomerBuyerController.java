package controller;

import main.Customer_App;
import org.json.JSONArray;
import org.json.JSONObject;
import structure.Order;
import structure.OrderItem;
import structure.Product;
import view.CustomerBuyerProduct;
import view.CustomerBuyerView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class CustomerBuyerController implements ActionListener {
    private CustomerBuyerView view;
    private Order order = null;
    private CustomerBuyerProduct customerBuyerView;

    public CustomerBuyerController(CustomerBuyerView view) {
        this.view = view;

        view.getBtnAdd().addActionListener(this);
        view.getBtnPay().addActionListener(this);
        view.getBtnLoadProducts().addActionListener(this);

        order = new Order();
        order.setCustomerID(Customer_App.getInstance().getCurrentUser().getUserID());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getBtnAdd())
            addProduct();
        else if (e.getSource() == view.getBtnPay())
            makeOrder();
        else if (e.getSource() == view.getBtnLoadProducts()) {
            this.customerBuyerView = new CustomerBuyerProduct();
            customerBuyerView.setVisible(true);
        }
    }

    private void addProduct() {
        try {
            String id = JOptionPane.showInputDialog("Enter ProductID: ");
            URL url = new URL("http://localhost:8000/inventory?productID=" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                JSONObject jsonProduct = new JSONObject(reader.readLine());
                reader.close();

                Product product = new Product();
                product.setProductID(jsonProduct.getInt("productID"));
                product.setName(jsonProduct.getString("name"));
                product.setPrice(jsonProduct.getDouble("unitPrice"));
                product.setQuantity(jsonProduct.getInt("quantity"));

                int quantity = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter quantity: "));
                if (quantity <= 0 || quantity > product.getQuantity()) {
                    JOptionPane.showMessageDialog(null, "Invalid quantity!");
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

                view.addRow(row);
                view.getLabTotal().setText("Total: $" + order.getTotalCost());
                view.invalidate();
            } else {
                JOptionPane.showMessageDialog(null, "Product not found!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private void makeOrder() {
        if (order.getLines().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please select at least one product.");
            return;
        }

        try {
            String address = JOptionPane.showInputDialog(view, "Enter shipping address:");
            if (address == null || address.trim().isEmpty()) {
                return;
            }

            String selectedShipper = JOptionPane.showInputDialog(view, "Enter shipper name:");
            if (selectedShipper == null || selectedShipper.trim().isEmpty()) {
                return;
            }

            JSONObject jsonOrder = new JSONObject();
            order.setAddress(address);
            order.setCompanyName(selectedShipper);
            order.setDate(Timestamp.valueOf(LocalDateTime.now()));
            jsonOrder.put("customerID", Customer_App.getInstance().getCurrentUser().getUserID());
            jsonOrder.put("address", order.getAddress());
            jsonOrder.put("shipper", order.getShipperName());
            jsonOrder.put("orderDate", order.getDate());
            jsonOrder.put("totalPrice", order.getTotalCost());
            jsonOrder.put("totalItem", order.getLines().size());

            JSONArray items = new JSONArray();
            for (OrderItem line : order.getLines()) {
                JSONObject jsonItem = new JSONObject();
                jsonItem.put("productID", line.getProductID());
                jsonItem.put("productName", line.getProductName());
                jsonItem.put("quantity", line.getQuantity());
                jsonItem.put("unitPrice", line.getUnitCost());
                jsonItem.put("totalPrice", line.getCost());
                items.put(jsonItem);
            }
            jsonOrder.put("Item", items);

            URL url = new URL("http://localhost:8000/orderCustomer");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            System.out.println("Request Method: " + connection.getRequestMethod());
            System.out.println("Request Property Content-Type: " + connection.getRequestProperty("Content-Type"));

            if (connection == null) {
                throw new IOException("Failed to initialize connection object.");
            }

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonOrder.toString().getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (connection.getResponseCode() == 201) {
                JOptionPane.showMessageDialog(null, "Order placed successfully!");
                Customer_App.getInstance().getCustomerOrderReportController().refreshOrder();
                view.clearOrder();
                order = new Order();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to place order.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
}
