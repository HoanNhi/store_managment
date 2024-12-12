package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import structure.User;
import adapter.DataAdapterInterface;  // Use your interface

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

public class UserQuery implements HttpHandler {

    private DataAdapterInterface dataAdapter;

    public UserQuery(DataAdapterInterface dataAdapter) { // Inject DataAdapterInterface
        this.dataAdapter = dataAdapter;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) { // Handle GET requests
            URI requestURI = exchange.getRequestURI();
            Map<String, String> queryParams = Utils.parseQueryParams(requestURI.getQuery()); // Assuming you have a Utils class


            String username = queryParams.get("username");
            String password = queryParams.get("password"); // Assuming you have a Utils class

            if (username != null && password != null) { // Check if both parameters are provided


                User user = dataAdapter.loadUser(username, password);

                if (user != null) {
                    // User found - return user details as JSON


                    JSONObject jsonResponse = new JSONObject();

                    jsonResponse.put("userID", user.getUserID());
                    jsonResponse.put("firstName", user.getFirstName());
                    jsonResponse.put("lastName", user.getLastName());
                    jsonResponse.put("phone", user.getPhone());
                    jsonResponse.put("email", user.getEmail());
                    jsonResponse.put("address", user.getAddress());
                    jsonResponse.put("role", user.getRole().name());


                    String responseString = jsonResponse.toString();


                    sendJsonResponse(exchange, 200, responseString);

                } else {
                    sendJsonResponse(exchange, 404, "{ \"message\": \"User not found\" }"); // 404 Not Found

                }

            }

            else {
                // Invalid request (missing parameters) - return 400 Bad Request
                sendJsonResponse(exchange, 400, "{ \"message\": \"Missing username or password\" }");
            }



        } else {
            // Method not allowed - return 405
            System.out.println("Unsupported request method: " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
            sendResponse(exchange, 405, "Method Not Allowed");
        }

        exchange.close();
    }


    private void sendJsonResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json"); // Set JSON content type
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