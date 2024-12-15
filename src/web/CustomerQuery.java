package web;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import adapter.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CustomerQuery implements HttpHandler {

    private final DataAdapterInterface dataAdapterMongo;

    public CustomerQuery(DataAdapterInterface dataAdapterMongo) {
        this.dataAdapterMongo = dataAdapterMongo;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());

        try {
            if (queryParams.containsKey("customerID")) {
                int customerID = Integer.parseInt(queryParams.get("customerID"));

                // Query MongoDB for a specific customer
                Document customerDoc = dataAdapterMongo.loadCustomerDoc(customerID);

                if (customerDoc == null) {
                    sendResponse(exchange, 404, "Customer not found");
                    return;
                }

                // Convert MongoDB document to JSON response
                JSONObject customerJson = new JSONObject(customerDoc.toJson());
                sendResponse(exchange, 200, customerJson.toString());
            } else {
                // Query MongoDB for all customers
                MongoCollection<Document> collection = dataAdapterMongo.getCustomerCollection();
                MongoCursor<Document> cursor = collection.find().iterator();

                JSONArray customersArray = new JSONArray();

                while (cursor.hasNext()) {
                    Document customerDoc = cursor.next();
                    JSONObject customerJson = new JSONObject(customerDoc.toJson());
                    customersArray.put(customerJson);
                }

                sendResponse(exchange, 200, customersArray.toString());
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> queryParams = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return queryParams;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                queryParams.put(key, value);
            }
        }
        return queryParams;
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
