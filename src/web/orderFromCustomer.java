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

public class orderFromCustomer implements HttpHandler {

    private final DataAdapterInterface dataAdapterMongo;
    private final DataAdapterInterface dataAdapter;

    public orderFromCustomer(DataAdapterInterface dataAdapterMongo, DataAdapterInterface dataAdapter) {
        this.dataAdapterMongo = dataAdapterMongo;
        this.dataAdapter = dataAdapter;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGetProducts(exchange);
                break;
            case "POST":
                handlePostOrder(exchange);
                break;
            default:
                sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private void handleGetProducts(HttpExchange exchange) throws IOException {
        List<Product> products = dataAdapter.loadAllProducts();
        JSONArray jsonArray = new JSONArray();

        for (Product product : products) {
            JSONObject jsonProduct = new JSONObject();
            jsonProduct.put("productID", product.getProductID());
            jsonProduct.put("name", product.getName());
            jsonProduct.put("category", product.getCategory());
            jsonProduct.put("supplierID", product.getSupplierID());
            jsonProduct.put("price", product.getPrice());
            jsonProduct.put("description", product.getDescription());
            jsonProduct.put("quantity", product.getQuantity());
            jsonArray.put(jsonProduct);
        }

        sendJsonResponse(exchange, 200, jsonArray.toString());
    }

    private void handlePostOrder(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            JSONObject jsonOrder = new JSONObject(body);

            // Validate required fields
            if (!jsonOrder.has("customerID") || !jsonOrder.has("address") || !jsonOrder.has("shipper") || !jsonOrder.has("Item")) {
                sendResponse(exchange, 400, "Bad Request: Missing required fields");
                return;
            }

            Order order = new Order();
            order.setCustomerID(jsonOrder.getInt("customerID"));
            order.setAddress(jsonOrder.getString("address"));
            order.setCompanyName(jsonOrder.getString("shipper"));
            order.setDate(Timestamp.valueOf(jsonOrder.getString("orderDate")));

            JSONArray items = jsonOrder.getJSONArray("Item");
            for (int i = 0; i < items.length(); i++) {
                JSONObject jsonItem = items.getJSONObject(i);
                if (!jsonItem.has("productID") || !jsonItem.has("quantity")) {
                    sendResponse(exchange, 400, "Bad Request: Missing productID or quantity in items");
                    return;
                }

                int productID = jsonItem.getInt("productID");
                int quantity = jsonItem.getInt("quantity");

                Product product = dataAdapter.loadProduct(productID);
                if (product == null || product.getQuantity() < quantity) {
                    sendResponse(exchange, 400, "Invalid product or insufficient stock");
                    return;
                }

                // Update product quantity
                product.setQuantity(product.getQuantity() - quantity);
                dataAdapter.saveProduct(product);

                OrderItem item = new OrderItem();
                item.setProductID(productID);
                item.setProductName(product.getName());
                item.setQuantity(quantity);
                item.setUnitCost(product.getPrice());
                item.setCost(quantity * product.getPrice());
                order.addLine(item);
                order.setTotalCost(order.getTotalCost() + item.getCost());
            }

            boolean success = dataAdapterMongo.saveOrder(order);
            sendResponse(exchange, success ? 201 : 500, success ? "Order created successfully" : "Failed to create order");
        } catch (Exception e) {
            sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
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
