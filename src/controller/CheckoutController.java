package controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import adapter.DataAdapterMongo;
import org.json.JSONArray;
import org.json.JSONObject;
import structure.Order;
import structure.OrderItem;
import structure.Product;
import view.BuyerView;
import view.CustomerView;
import adapter.DataAdapter;

public class CheckoutController implements ActionListener {
    private BuyerView view;
    private Order order = null;
    private CustomerView customerView; // Add customer dialog

    public CheckoutController(BuyerView view, DataAdapter dataAdapter, DataAdapterMongo dataAdapterMongo) {
        this.view = view;
        this.customerView = new CustomerView(view);

        view.getBtnAdd().addActionListener(this);
        view.getBtnPay().addActionListener(this);

        order = new Order();
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getBtnAdd())
            addProduct();
        else
        if (e.getSource() == view.getBtnPay()) {
            customerView.setVisible(true);
            if (customerView.getCustomer() != null)
                makeOrder();
            else
                JOptionPane.showMessageDialog(view, "Please select customer!");
        }
    }

    private void makeOrder() {
        try {
            JSONObject orderJson = new JSONObject();
            orderJson.put("customerID", customerView.getCustomer().getCustomerID());
            orderJson.put("totalCost", order.getTotalCost());
            orderJson.put("address", customerView.getAddress());
            orderJson.put("companyName", customerView.getCompanyName());

            JSONArray items = new JSONArray();
            for (OrderItem item : order.getLines()) {
                JSONObject itemJson = new JSONObject();
                itemJson.put("productID", item.getProductID());
                itemJson.put("productName", item.getProductName());
                itemJson.put("quantity", item.getQuantity());
                itemJson.put("unitCost", item.getUnitCost());
                itemJson.put("cost", item.getCost());
                items.put(itemJson);
            }
            orderJson.put("items", items);

            URL url = new URL("http://localhost:8000/orderShop");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(orderJson.toString().getBytes(StandardCharsets.UTF_8));
            }

            if (connection.getResponseCode() == 201) {
                JOptionPane.showMessageDialog(view, "Order added successfully");
                view.clearOrder();
                order = new Order();
            } else {
                JOptionPane.showMessageDialog(view, "Failed to create order");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
        }
    }

    private void addProduct() {
        try {
            String id = JOptionPane.showInputDialog("Enter ProductID: ");
            if (id == null || id.trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Product ID cannot be empty!");
                return;
            }

            // Make an API call to fetch product details
            URL url = new URL("http://localhost:8000/inventory?productID=" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String lineResponse;
                while ((lineResponse = reader.readLine()) != null) {
                    response.append(lineResponse);
                }

                // Parse the product details
                JSONObject productJson = new JSONObject(response.toString());
                Product product = new Product();
                product.setProductID(productJson.getInt("productID"));
                product.setName(productJson.getString("name"));
                product.setPrice(productJson.getDouble("unitPrice"));
                product.setQuantity(productJson.getInt("quantity"));

                // Prompt for quantity
                int quantity = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter quantity: "));
                if (quantity <= 0 || quantity > product.getQuantity()) {
                    JOptionPane.showMessageDialog(null, "This quantity is not valid!");
                    return;
                }

                // Create a new order line
                OrderItem line = new OrderItem();
                line.setProductID(product.getProductID());
                line.setProductName(product.getName());
                line.setQuantity(quantity);
                line.setUnitCost(product.getPrice());
                line.setCost(quantity * product.getPrice());
                order.addLine(line);
                order.setTotalCost(order.getTotalCost() + line.getCost());

                // Update the table view
                Object[] row = new Object[5];
                row[0] = line.getProductID();
                row[1] = product.getName();
                row[2] = product.getPrice();
                row[3] = line.getQuantity();
                row[4] = line.getCost();

                this.view.addRow(row);
                this.view.getLabTotal().setText("Total: $" + order.getTotalCost());
                this.view.invalidate();
            } else {
                JOptionPane.showMessageDialog(view, "Product not found or API call failed. Response code: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
        }
    }


}