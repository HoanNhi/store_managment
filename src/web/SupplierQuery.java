package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import adapter.*;
import structure.Supplier;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SupplierQuery implements HttpHandler {

    private final DataAdapterInterface dataAdapter;

    public SupplierQuery(DataAdapterInterface dataAdapter) {
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
                handleSave(exchange);
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
        if (query != null && query.contains("supplierID=")) {
            int supplierID = Integer.parseInt(query.split("=")[1]);
            Supplier supplier = dataAdapter.loadSupplier(supplierID);

            if (supplier != null) {
                JSONObject json = new JSONObject();
                json.put("supplierID", supplier.getSupplierID());
                json.put("name", supplier.getName());
                json.put("contactPerson", supplier.getContactPerson());
                json.put("phone", supplier.getPhone());

                sendJsonResponse(exchange, 200, json.toString());
            } else {
                sendResponse(exchange, 404, "Supplier not found");
            }
        } else {
            // Retrieve all suppliers
            JSONArray jsonArray = new JSONArray();
            for (Supplier supplier : dataAdapter.loadAllSuppliers()) {
                JSONObject json = new JSONObject();
                json.put("supplierID", supplier.getSupplierID());
                json.put("name", supplier.getName());
                json.put("contactPerson", supplier.getContactPerson());
                json.put("phone", supplier.getPhone());
                jsonArray.put(json);
            }
            sendJsonResponse(exchange, 200, jsonArray.toString());
        }
    }

    private void handleSave(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(body);

        Supplier supplier = new Supplier();
        supplier.setSupplierID(json.getInt("supplierID"));
        supplier.setName(json.getString("name"));
        supplier.setContactPerson(json.getString("contactPerson"));
        supplier.setPhone(json.getString("phone"));

        boolean success = dataAdapter.saveSupplier(supplier);
        sendResponse(exchange, success ? 200 : 500, success ? "Supplier saved successfully" : "Failed to save supplier");
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.contains("supplierID=")) {
            int supplierID = Integer.parseInt(query.split("=")[1]);

            boolean success = dataAdapter.deleteSupplier(supplierID);
            sendResponse(exchange, success ? 200 : 500, success ? "Supplier deleted successfully" : "Failed to delete supplier");
        } else {
            sendResponse(exchange, 400, "Missing or invalid supplierID");
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
