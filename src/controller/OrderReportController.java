// OrderReportController.java
package controller;


import adapter.*;
import main.Application;
import org.json.JSONArray;
import org.json.JSONObject;
import structure.Order;
import structure.User;
import view.OrderReportView;
import view.OrderDetailView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

public class OrderReportController implements ActionListener {


    private DataAdapterInterface dataAdapter;
    private OrderReportView orderReportView;
    private OrderDetailView orderDetailView;


    public OrderReportController(OrderReportView reportView, OrderDetailView detailView, DataAdapterInterface dataAdapter) {
        this.dataAdapter = dataAdapter;
        this.orderReportView = reportView;
        this.orderDetailView = detailView;
        orderReportView.getBtnViewDetails().addActionListener(this);
        orderReportView.getBtnDelete().addActionListener(this);
        orderReportView.getBtnFilterByDate().addActionListener(this);

        // Load and display order data
        List<Order> orders = dataAdapter.loadAllOrders();

        if (Application.getInstance().getCurrentUser().getRole() == User.UserRole.Customer){
            orders = orders.stream()
                    .filter(order -> order.getCustomerID() == Application.getInstance().getCurrentUser().getUserID())
                    .collect(Collectors.toList());
        }
        if (orders != null){
            System.out.println("Total order "+ orders.size());
            orderReportView.setOrders(orders);
        }
        orderReportView.getOrderTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) { // Check for left-click
                    JTable target = (JTable)e.getSource();
                    int selectedRow = target.getSelectedRow();
                    if (selectedRow != -1) { // Check if a row is selected
                        try {
                            int orderID = (int) target.getValueAt(selectedRow, 0); // Get order ID from first column
                            orderReportView.getTxtOrderID().setText(String.valueOf(orderID)); // Set in text field
                        } catch (ClassCastException ex) { //Handle potential ClassCastException
                            JOptionPane.showMessageDialog(orderReportView, "Invalid data format in the selected cell.");
                        }

                    }
                }
            }
        });
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

    private void showOrderDetails(int orderId){
        Order order = dataAdapter.loadOrder(orderId); //Load order based on selected order id

        if (order != null) {
            orderDetailView.setOrder(order); //Set the order in the orderDetailView
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
                List<Order> updatedOrders = loadOrdersFromAPI(); // Reload orders after deletion
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
                        order.setShipperName(obj.getString("shipper"));
                        orders.add(order);
                    }
                }
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
                        order.setShipperName(obj.getString("shipper"));
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