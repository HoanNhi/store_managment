package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import adapter.DataAdapterInterface;
import structure.Customer;
import structure.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UserManagement implements HttpHandler {
    private final DataAdapterInterface dataAdapterSQL;
    private final DataAdapterInterface dataAdapterMongo;

    public UserManagement(DataAdapterInterface dataAdapterSQL, DataAdapterInterface dataAdapterMongo) {
        this.dataAdapterSQL = dataAdapterSQL;
        this.dataAdapterMongo = dataAdapterMongo;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGetUsers(exchange);
                break;
            case "POST":
                handleCreateUser(exchange);
                break;
            case "PUT":
                handleUpdateUser(exchange);
                break;
            case "DELETE":
                handleDeleteUser(exchange);
                break;
            default:
                sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private void handleGetUsers(HttpExchange exchange) throws IOException {
        try {
            // Retrieve all users from the SQL database
            List<User> users = dataAdapterSQL.loadAllUsers();

            // Convert users to JSON array
            JSONArray jsonArray = new JSONArray();
            for (User user : users) {
                JSONObject userJson = new JSONObject();
                userJson.put("userID", user.getUserID());
                userJson.put("username", user.getUsername());
                userJson.put("firstName", user.getFirstName());
                userJson.put("lastName", user.getLastName());
                userJson.put("email", user.getEmail());
                userJson.put("phone", user.getPhone());
                userJson.put("address", user.getAddress());
                userJson.put("role", user.getRole().name());
                jsonArray.put(userJson);
            }

            // Send the JSON array as the response
            sendJsonResponse(exchange, 200, jsonArray.toString());
        } catch (Exception e) {
            sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    private void handleCreateUser(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(body);

        User user = parseUserFromJson(json);
        boolean success = dataAdapterSQL.addUser(user);

        if (success && user.getRole() == User.UserRole.Customer) {
            // If the role is Customer, create a MongoDB document
            Customer customer = parseCustomerFromUser(user);
            dataAdapterMongo.createCustomer(customer);
        }

        sendResponse(exchange, success ? 201 : 500, success ? "User created successfully" : "Failed to create user");
    }

    private void handleUpdateUser(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(body);

        User user = parseUserFromJson(json);
        boolean success = dataAdapterSQL.updateUser(user);

        if (success && user.getRole() == User.UserRole.Customer) {
            Customer customer = parseCustomerFromUser(user);
            dataAdapterMongo.updateCustomer(customer);
        }

        sendResponse(exchange, success ? 200 : 500, success ? "User updated successfully" : "Failed to update user");
    }

    private void handleDeleteUser(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null || !query.contains("userID=")) {
            sendResponse(exchange, 400, "Missing or invalid userID");
            return;
        }

        int userID = Integer.parseInt(query.split("=")[1]);
        User user = dataAdapterSQL.loadUser(userID);
        boolean success = dataAdapterSQL.deleteUser(userID);

        if (success && user != null && user.getRole() == User.UserRole.Customer) {
            dataAdapterMongo.deleteCustomer(userID);
        }

        sendResponse(exchange, success ? 200 : 500, success ? "User deleted successfully" : "Failed to delete user");
    }

    private User parseUserFromJson(JSONObject json) {
        User user = new User();
        user.setUserID(json.getInt("userID"));
        user.setUsername(json.getString("username"));
        user.setPassword(json.getString("password"));
        user.setFirstName(json.getString("firstName"));
        user.setLastName(json.getString("lastName"));
        user.setEmail(json.getString("email"));
        user.setPhone(json.getString("phone"));
        user.setAddress(json.getString("address"));
        user.setRole(User.UserRole.valueOf(json.getString("role")));
        return user;
    }

    private Customer parseCustomerFromUser(User user) {
        Customer customer = new Customer();
        customer.setUserID(user.getUserID());
        customer.setFirstName(user.getFirstName());
        customer.setLastName(user.getLastName());
        customer.setEmail(user.getEmail());
        customer.setPhone(user.getPhone());
        customer.setAddress(user.getAddress());
        return customer;
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
