package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import structure.Product;
import adapter.DataAdapterInterface;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class InventoryManagement implements HttpHandler {

    private final DataAdapterInterface dataSQL;

    public InventoryManagement(DataAdapterInterface dataSQL) {
        this.dataSQL = dataSQL;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        URI requestURI = exchange.getRequestURI();

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
                handleDelete(exchange, requestURI);
                break;
            default:
                sendResponse(exchange, 405, "Method Not Allowed");
        }
        exchange.close();
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        if (query != null && query.contains("productID=")) {
            // Extract the productID from the query parameters
            String[] params = query.split("&");
            String productIDParam = null;

            for (String param : params) {
                if (param.startsWith("productID=")) {
                    productIDParam = param.split("=")[1];
                    break;
                }
            }

            if (productIDParam != null) {
                try {
                    int productID = Integer.parseInt(productIDParam);
                    Product product = dataSQL.loadProduct(productID);

                    if (product != null) {
                        // Convert the product to JSON and send the response
                        JSONObject jsonProduct = new JSONObject();
                        jsonProduct.put("productID", product.getProductID());
                        jsonProduct.put("name", product.getName());
                        jsonProduct.put("category", product.getCategory());
                        jsonProduct.put("supplierID", product.getSupplierID());
                        jsonProduct.put("unitPrice", product.getPrice());
                        jsonProduct.put("description", product.getDescription());
                        jsonProduct.put("quantity", product.getQuantity());

                        sendJsonResponse(exchange, 200, jsonProduct.toString());
                    } else {
                        sendResponse(exchange, 404, "Product not found");
                    }
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "Invalid productID format");
                }
                return;
            }
        }

        // If no productID parameter, return all products
        List<Product> products = dataSQL.loadAllProducts();
        JSONArray jsonArray = new JSONArray();

        for (Product product : products) {
            JSONObject jsonProduct = new JSONObject();
            jsonProduct.put("productID", product.getProductID());
            jsonProduct.put("name", product.getName());
            jsonProduct.put("category", product.getCategory());
            jsonProduct.put("supplierID", product.getSupplierID());
            jsonProduct.put("unitPrice", product.getPrice());
            jsonProduct.put("description", product.getDescription());
            jsonProduct.put("quantity", product.getQuantity());
            jsonArray.put(jsonProduct);
        }

        sendJsonResponse(exchange, 200, jsonArray.toString());
    }


    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(body);

        Product product = new Product();
        product.setProductID(json.getInt("productID"));
        product.setName(json.getString("name"));
        product.setCategory(json.getString("category"));
        product.setSupplierID(json.getInt("supplierID"));
        product.setPrice(json.getDouble("unitPrice"));
        product.setDescription(json.getString("description"));
        product.setQuantity(json.getInt("quantity"));

        boolean success = dataSQL.saveProduct(product);
        sendResponse(exchange, success ? 201 : 500, success ? "Product created successfully" : "Failed to create product");
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(body);

        Product product = new Product();
        product.setProductID(json.getInt("productID"));
        product.setName(json.getString("name"));
        product.setCategory(json.getString("category"));
        product.setSupplierID(json.getInt("supplierID"));
        product.setPrice(json.getDouble("unitPrice"));
        product.setDescription(json.getString("description"));
        product.setQuantity(json.getInt("quantity"));

        boolean success = dataSQL.saveProduct(product);
        sendResponse(exchange, success ? 200 : 500, success ? "Product updated successfully" : "Failed to update product");
    }

    private void handleDelete(HttpExchange exchange, URI requestURI) throws IOException {
        String query = requestURI.getQuery();
        if (query == null || !query.contains("productID=")) {
            sendResponse(exchange, 400, "Missing or invalid productID");
            return;
        }

        int productID = Integer.parseInt(query.split("=")[1]);
        boolean success = dataSQL.deleteProduct(productID);
        sendResponse(exchange, success ? 200 : 500, success ? "Product deleted successfully" : "Failed to delete product");
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
