package web;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import adapter.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ShipperQuery implements HttpHandler {

    private final DataAdapterInterface dataAdapterMongo;

    public ShipperQuery(DataAdapterInterface dataAdapterMongo) {
        this.dataAdapterMongo = dataAdapterMongo;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            switch (method) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handleCreate(exchange);
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
        } catch (Exception e) {
            sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.contains("shipperID")) {
            String shipperIDParam = query.split("=")[1];
            int shipperID = Integer.parseInt(shipperIDParam);

            // Fetch a specific shipper
            Document shipperDoc = dataAdapterMongo.loadShipper(shipperID);

            if (shipperDoc != null) {
                JSONObject shipperJson = new JSONObject(shipperDoc.toJson());
                sendResponse(exchange, 200, shipperJson.toString());
            } else {
                sendResponse(exchange, 404, "Shipper not found");
            }
        } else {
            // Fetch all shippers
            MongoCollection<Document> collection = dataAdapterMongo.loadAllShippersDoc();
            MongoCursor<Document> cursor = collection.find().iterator();

            JSONArray shipperArray = new JSONArray();

            while (cursor.hasNext()) {
                Document shipperDoc = cursor.next();
                JSONObject shipperJson = new JSONObject(shipperDoc.toJson());
                shipperArray.put(shipperJson);
            }

            sendResponse(exchange, 200, shipperArray.toString());
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        JSONObject requestJson = new JSONObject(requestBody.toString());
        int shipperID = requestJson.getInt("shipperID");
        String companyName = requestJson.optString("companyName");
        double pricePerKM = requestJson.optDouble("pricePerKM", -1);

        Document updateDoc = new Document();
        if (!companyName.isEmpty()) {
            updateDoc.append("companyName", companyName);
        }
        if (pricePerKM >= 0) {
            updateDoc.append("pricePerKM", pricePerKM);
        }

        if (!updateDoc.isEmpty()) {
            dataAdapterMongo.loadAllShippersDoc().updateOne(Filters.eq("shipperID", shipperID), new Document("$set", updateDoc));
            sendResponse(exchange, 200, "Shipper updated successfully");
        } else {
            sendResponse(exchange, 400, "Bad Request: No fields to update");
        }
    }

    private void handleCreate(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        try {
            JSONObject requestJson = new JSONObject(requestBody.toString());
            int shipperID = requestJson.getInt("shipperID");
            String companyName = requestJson.getString("companyName");
            double pricePerKM = requestJson.getDouble("pricePerKM");

            Document newShipperDoc = new Document("shipperID", shipperID)
                    .append("companyName", companyName)
                    .append("pricePerKM", pricePerKM);

            dataAdapterMongo.loadAllShippersDoc().insertOne(newShipperDoc);

            sendResponse(exchange, 201, "Shipper created successfully");
        } catch (Exception e) {
            sendResponse(exchange, 400, "Bad Request: " + e.getMessage());
        }
    }


    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.contains("shipperID")) {
            String shipperIDParam = query.split("=")[1];
            int shipperID = Integer.parseInt(shipperIDParam);

            dataAdapterMongo.deleteShipper(shipperID);
            sendResponse(exchange, 200, "Shipper deleted successfully");
        } else {
            sendResponse(exchange, 400, "Bad Request: Missing shipperID parameter");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
}
