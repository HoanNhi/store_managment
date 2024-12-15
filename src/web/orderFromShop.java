package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import adapter.*;
import org.json.JSONArray;
import org.json.JSONObject;
import structure.Order;
import structure.OrderItem;
import structure.Product;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class orderFromShop implements HttpHandler {

    private final DataAdapterInterface dataAdapterMongo;
    private final DataAdapterInterface dataAdapter;

    public orderFromShop(DataAdapterInterface dataAdapterMongo, DataAdapterInterface dataAdapter) {
        this.dataAdapterMongo = dataAdapterMongo;
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
            default:
                sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        if (query != null && query.contains("orderID=")) {
            int orderID = Integer.parseInt(query.split("=")[1]);
            Order order = dataAdapterMongo.loadOrder(orderID);

            if (order != null) {
                sendJsonResponse(exchange, 200, orderToJson(order).toString());
            } else {
                sendResponse(exchange, 404, "Order not found");
            }
        } else {
            List<Order> orders = dataAdapterMongo.loadAllOrders();
            JSONArray jsonArray = new JSONArray();

            for (Order order : orders) {
                jsonArray.put(orderToJson(order));
            }

            sendJsonResponse(exchange, 200, jsonArray.toString());
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(body);

        Order order = jsonToOrder(json);

        for (OrderItem item : order.getLines()) {
            Product product = dataAdapter.loadProduct(item.getProductID());
            if (product == null || item.getQuantity() > product.getQuantity()) {
                sendResponse(exchange, 400, "Invalid product or insufficient stock");
                return;
            }

            product.setQuantity(product.getQuantity() - item.getQuantity());
            dataAdapter.saveProduct(product);
        }

        boolean success = dataAdapterMongo.saveOrder(order);
        sendResponse(exchange, success ? 201 : 500, success ? "Order created successfully" : "Failed to create order");
    }

    private JSONObject orderToJson(Order order) {
        JSONObject json = new JSONObject();
        json.put("orderID", order.getOrderID());
        json.put("customerID", order.getCustomerID());
        json.put("date", order.getDate().toString());
        json.put("totalCost", order.getTotalCost());
        json.put("address", order.getAddress());
        json.put("shipperName", order.getShipperName());

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
        json.put("items", items);
        return json;
    }

    private Order jsonToOrder(JSONObject json) {
        Order order = new Order();
//        order.setOrderID(json.getInt("orderID"));
        order.setCustomerID(json.getInt("customerID"));
        order.setDate(Timestamp.valueOf(LocalDateTime.now()));
        order.setTotalCost(json.getDouble("totalCost"));
        order.setAddress(json.getString("address"));
        order.setCompanyName(json.getString("companyName"));

        JSONArray items = json.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject itemJson = items.getJSONObject(i);
            OrderItem item = new OrderItem();
            item.setProductID(itemJson.getInt("productID"));
            item.setProductName(itemJson.getString("productName"));
            item.setQuantity(itemJson.getInt("quantity"));
            item.setUnitCost(itemJson.getDouble("unitCost"));
            item.setCost(itemJson.getDouble("cost"));
            order.addLine(item);
        }
        return order;
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
