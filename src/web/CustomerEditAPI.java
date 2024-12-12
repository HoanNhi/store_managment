package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import adapter.*;
import org.json.JSONObject;
import structure.Customer;
import structure.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CustomerEditAPI implements HttpHandler {

    private final DataAdapterInterface dataAdapterMongo;
    private final DataAdapterInterface dataAdapterSQL;

    public CustomerEditAPI(DataAdapterInterface dataAdapterMongo, DataAdapterInterface dataAdapterSQL) {
        this.dataAdapterMongo = dataAdapterMongo;
        this.dataAdapterSQL = dataAdapterSQL;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "PUT":
                    handlePut(exchange);
                    break;
                default:
                    sendResponse(exchange, 405, "Method Not Allowed");
            }
        } catch (Exception e) {
            try {
                sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
            } catch (Exception ignored) {
            }
        }
    }

    private void handleGet(HttpExchange exchange) throws Exception {
        String query = exchange.getRequestURI().getQuery();
        if (query == null || !query.contains("customerID=")) {
            sendResponse(exchange, 400, "Missing or invalid customerID");
            return;
        }

        int customerID = Integer.parseInt(query.split("=")[1]);
        Customer customer = dataAdapterMongo.loadCustomer(customerID);
        User user = dataAdapterSQL.loadUser(customerID);

        if (customer == null || user == null) {
            sendResponse(exchange, 404, "Customer not found");
            return;
        }

        JSONObject response = new JSONObject();
        response.put("customer", customerToJson(customer));
        response.put("user", userToJson(user));

        sendJsonResponse(exchange, 200, response.toString());
    }

    private void handlePut(HttpExchange exchange) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        JSONObject requestJson = new JSONObject(requestBody.toString());
        JSONObject customerJson = requestJson.getJSONObject("customer");
        JSONObject userJson = requestJson.getJSONObject("user");

        Customer customer = jsonToCustomer(customerJson);
        User user = jsonToUser(userJson);

        boolean mongoSuccess = dataAdapterMongo.updateCustomer(customer);
        boolean sqlSuccess = dataAdapterSQL.updateUser(user);

        if (mongoSuccess && sqlSuccess) {
            sendResponse(exchange, 200, "Customer information updated successfully");
        } else {
            sendResponse(exchange, 500, "Failed to update customer information");
        }
    }

    private JSONObject customerToJson(Customer customer) {
        JSONObject json = new JSONObject();
        json.put("customerID", customer.getCustomerID());
        json.put("userID", customer.getUserID());
        json.put("firstName", customer.getFirstName());
        json.put("lastName", customer.getLastName());
        json.put("email", customer.getEmail());
        json.put("phone", customer.getPhone());
        json.put("address", customer.getAddress());

        JSONObject card = new JSONObject();
        card.put("cardNumber", customer.getCardNumber());
        card.put("csv", customer.getCsv());
        card.put("expiryYear", customer.getExpiryYear());
        card.put("expiryMonth", customer.getExpiryMonth());
        card.put("cardType", customer.getCardType());
        json.put("card", card);

        return json;
    }

    private JSONObject userToJson(User user) {
        JSONObject json = new JSONObject();
        json.put("userID", user.getUserID());
        json.put("username", user.getUsername());
        json.put("password", user.getPassword());
        json.put("firstName", user.getFirstName());
        json.put("lastName", user.getLastName());
        json.put("email", user.getEmail());
        json.put("phone", user.getPhone());
        json.put("address", user.getAddress());
        json.put("role", user.getRole());
        return json;
    }

    private Customer jsonToCustomer(JSONObject json) {
        Customer customer = new Customer();
        customer.setCustomerID(json.getInt("customerID"));
        customer.setUserID(json.getInt("userID"));
        customer.setFirstName(json.getString("firstName"));
        customer.setLastName(json.getString("lastName"));
        customer.setEmail(json.getString("email"));
        customer.setPhone(json.getString("phone"));
        customer.setAddress(json.getString("address"));

        JSONObject card = json.getJSONObject("card");
        customer.setCard(
                card.getString("cardNumber"),
                card.getString("csv"),
                card.getString("expiryYear"),
                card.getString("expiryMonth"),
                card.getString("cardType")
        );

        return customer;
    }

    private User jsonToUser(JSONObject json) {
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

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String response) throws Exception {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws Exception {
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
