package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import adapter.*;
import structure.Order;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.List;

public class SaleManagement implements HttpHandler {
    private final DataAdapterInterface dataAdapter;

    public SaleManagement(DataAdapterInterface dataAdapter) {
        this.dataAdapter = dataAdapter;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "PUT":
                handlePut(exchange);
                break;
            case "DELETE":
                handleDelete(exchange);
                break;
            default:
                sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        if (query != null && query.contains("startDate") && query.contains("endDate")) {
            // Extract startDate and endDate from query parameters
            String[] params = query.split("&");
            String startDateStr = null, endDateStr = null;

            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue[0].equals("startDate")) {
                    startDateStr = keyValue[1];
                } else if (keyValue[0].equals("endDate")) {
                    endDateStr = keyValue[1];
                }
            }

            if (startDateStr != null && endDateStr != null) {
                try {
                    // Parse the dates
                    Date startDate = Date.valueOf(startDateStr); // Assumes yyyy-MM-dd format
                    Date endDate = Date.valueOf(endDateStr);

                    // Filter orders by date range
                    List<Order> orders = dataAdapter.loadOrdersByDateRange(startDate, endDate);
                    JSONArray jsonArray = new JSONArray();

                    for (Order order : orders) {
                        JSONObject orderJson = new JSONObject();
                        orderJson.put("orderID", order.getOrderID());
                        orderJson.put("customerID", order.getCustomerID());
                        orderJson.put("orderDate", order.getDate().toString());
                        orderJson.put("totalPrice", order.getTotalCost());
                        orderJson.put("address", order.getAddress());
                        orderJson.put("shipper", order.getShipperName());
                        jsonArray.put(orderJson);
                    }

                    sendJsonResponse(exchange, 200, jsonArray.toString());
                    return;

                } catch (IllegalArgumentException e) {
                    sendResponse(exchange, 400, "Invalid date format. Use yyyy-MM-dd.");
                    return;
                }
            }
        }

        // If no date range specified, return all orders
        List<Order> orders = dataAdapter.loadAllOrders();
        JSONArray jsonArray = new JSONArray();

        for (Order order : orders) {
            JSONObject orderJson = new JSONObject();
            orderJson.put("orderID", order.getOrderID());
            orderJson.put("customerID", order.getCustomerID());
            orderJson.put("orderDate", order.getDate().toString());
            orderJson.put("totalPrice", order.getTotalCost());
            orderJson.put("address", order.getAddress());
            orderJson.put("shipper", order.getShipperName());
            jsonArray.put(orderJson);
        }

        sendJsonResponse(exchange, 200, jsonArray.toString());
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(body);

        Order order = new Order();
        order.setOrderID(json.getInt("orderID"));
        order.setCustomerID(json.getInt("customerID"));
        order.setTotalCost(json.getDouble("totalPrice"));
        order.setAddress(json.getString("address"));
        order.setShipperName(json.getString("shipper"));

        boolean success = dataAdapter.saveOrder(order);
        sendResponse(exchange, success ? 201 : 500, success ? "Order created successfully" : "Failed to create order");
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(body);

        Order order = new Order();
        order.setOrderID(json.getInt("orderID"));
        order.setCustomerID(json.getInt("customerID"));
        order.setTotalCost(json.getDouble("totalPrice"));
        order.setAddress(json.getString("address"));
        order.setShipperName(json.getString("shipper"));

        boolean success = dataAdapter.saveOrder(order); // MongoDB uses the same method for update
        sendResponse(exchange, success ? 200 : 500, success ? "Order updated successfully" : "Failed to update order");
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null || !query.contains("orderID=")) {
            sendResponse(exchange, 400, "Missing or invalid orderID");
            return;
        }

        int orderID = Integer.parseInt(query.split("=")[1]);
        boolean success = dataAdapter.deleteOrder(orderID);
        sendResponse(exchange, success ? 200 : 500, success ? "Order deleted successfully" : "Failed to delete order");
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
