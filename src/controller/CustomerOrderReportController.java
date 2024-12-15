// OrderReportController.java
package controller;


import adapter.DataAdapterInterface;
import main.Customer_App;
import main.Customer_App;
import org.json.JSONArray;
import org.json.JSONObject;
import structure.Order;
import structure.OrderItem;
import structure.User;
import view.CustomerOrderDetailView;
import view.CustomerOrderReportView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerOrderReportController implements ActionListener {

    private CustomerOrderReportView orderReportView;
    private CustomerOrderDetailView orderDetailView;
    List<Order> orders = new ArrayList<>();

    public CustomerOrderReportController(CustomerOrderReportView reportView, CustomerOrderDetailView detailView) {
        this.orderReportView = reportView;
        this.orderDetailView = detailView;
        orderReportView.getBtnViewDetails().addActionListener(this);
        orderReportView.getBtnDelete().addActionListener(this);
        orderReportView.getBtnFilterByDate().addActionListener(this);

        // Load and display order data
        orders = loadOrdersFromAPI().stream()
                .filter(order -> order.getCustomerID() == Customer_App.getInstance().getCurrentUser().getUserID())
                .collect(Collectors.toList());
        if (orders != null){
            System.out.println("Total order "+ orders.size());
            orderReportView.setOrders(orders);
        }
        else{
            orders = new ArrayList<>();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == orderReportView.getBtnViewDetails()) {
            try {
                int orderId = Integer.parseInt(orderReportView.getTxtOrderID().getText());
                showOrderDetails(orderId);
                orderReportView.clearOrderIDField();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(orderReportView, "Invalid Order ID format. Please enter a number.");
            }
        }
        if (e.getSource() == orderReportView.getBtnDelete()) {
            try {
                int orderID = Integer.parseInt(orderReportView.getTxtOrderID().getText());
                deleteOrder(orderID);
                orderReportView.setOrders(loadOrdersFromAPI());

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(orderReportView, "Invalid Order ID format. Please enter a number.");
            }
        }

        if (e.getSource() == orderReportView.getBtnFilterByDate()) {
            Date startDate = orderReportView.getStartDate();
            Date endDate = orderReportView.getEndDate();

            if (startDate != null && endDate != null) {
                filterOrdersByDateRange(startDate, endDate);
            } else {
                JOptionPane.showMessageDialog(orderReportView, "Invalid Date input. Please ensure the dates are in yyyy-MM-dd format.");
            }
        }
    }

    public void refreshOrder(){
        this.orders = loadOrdersFromAPI().stream()
                .filter(order -> order.getCustomerID() == Customer_App.getInstance().getCurrentUser().getUserID())
                .collect(Collectors.toList());
        orderReportView.setOrders(this.orders);
    }

    private void showOrderDetails(int orderId){
        Order order = this.orders.stream()
                .filter(o -> o.getOrderID() == orderId)
                .findFirst()
                .orElse(null);

        if (order != null) {
            orderDetailView.setOrder(order);
            orderDetailView.setVisible(true);

        } else {
            JOptionPane.showMessageDialog(orderReportView, "Error loading order details.");

        }
    }

    private void deleteOrder(int orderID) {
        try {
            URL url = new URL("http://localhost:8000/sales?orderID=" + orderID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                List<Order> updatedOrders = loadOrdersFromAPI();
                orderReportView.setOrders(updatedOrders);
                JOptionPane.showMessageDialog(orderReportView, "Order deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(orderReportView, "Failed to delete order. Response code: " + responseCode);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(orderReportView, "Error communicating with server: " + e.getMessage());
        }
    }

    private List<Order> loadOrdersFromAPI() {
        List<Order> orders = new ArrayList<>();
        try {
            URL url = new URL("http://localhost:8000/sales");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }

                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        Order order = new Order();
                        order.setOrderID(obj.getInt("orderID"));
                        order.setCustomerID(obj.getInt("customerID"));
                        order.setTotalCost(obj.getDouble("totalPrice"));
                        order.setAddress(obj.getString("address"));
                        order.setCompanyName(obj.getString("shipper"));
                        order.setDate(Timestamp.valueOf(obj.getString("orderDate")));
                        // Parse items array
                        JSONArray itemsArray = obj.getJSONArray("Item");
                        for (int j = 0; j < itemsArray.length(); j++) {
                            JSONObject itemObj = itemsArray.getJSONObject(j);
                            OrderItem item = new OrderItem();
                            item.setOrderItemID(itemObj.getInt("orderItemID"));
                            item.setProductID(itemObj.getInt("productID"));
                            item.setProductName(itemObj.getString("productName"));
                            item.setQuantity(itemObj.getInt("quantity"));
                            item.setUnitCost(itemObj.getDouble("unitCost"));
                            item.setCost(itemObj.getDouble("totalCost"));
                            order.addLine(item);
                        }

                        orders.add(order);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(orderReportView, "Failed to fetch orders. Response code: " + connection.getResponseCode());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(orderReportView, "Error communicating with the server: " + e.getMessage());
        }
        return orders;
    }

    private void filterOrdersByDateRange(Date startDate, Date endDate) {
        try {
            // Build the URL with date range query parameters
            String urlString = String.format(
                    "http://localhost:8000/sales?startDate=%s&endDate=%s",
                    startDate.toString(), // Convert to yyyy-MM-dd
                    endDate.toString()
            );
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            // Check the response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }

                    // Parse JSON response and update orders
                    JSONArray jsonArray = new JSONArray(response.toString());
                    List<Order> filteredOrders = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        Order order = new Order();
                        order.setOrderID(obj.getInt("orderID"));
                        order.setCustomerID(obj.getInt("customerID"));
                        order.setDate(Timestamp.valueOf(obj.getString("orderDate"))); // Assuming date is yyyy-MM-dd
                        order.setTotalCost(obj.getDouble("totalPrice"));
                        order.setAddress(obj.getString("address"));
                        order.setCompanyName(obj.getString("shipper"));
                        filteredOrders.add(order);
                    }

                    // Update the view with filtered orders
                    orderReportView.setOrders(filteredOrders);
                    JOptionPane.showMessageDialog(orderReportView, "Orders filtered by date range successfully.");

                }
            } else {
                JOptionPane.showMessageDialog(orderReportView, "Failed to filter orders. Response code: " + responseCode);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(orderReportView, "Error communicating with the server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}