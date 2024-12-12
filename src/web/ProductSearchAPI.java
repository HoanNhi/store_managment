package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import adapter.*;
import structure.Product;
import web.util.*;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductSearchAPI implements HttpHandler {

    private final DataAdapterInterface dataAdapter;

    public ProductSearchAPI(DataAdapterInterface dataAdapter) {
        this.dataAdapter = dataAdapter;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed", "Error");
            return;
        }

        URI uri = exchange.getRequestURI();
        Map<String, String> queryParams = parseQueryParams(uri.getQuery());

        try {
            List<Product> products = fetchProducts(queryParams);

            // Use Builder pattern to generate HTML page
            String htmlPage = new HTMLBuilder()
                    .setTitle("Product Search Results")
                    .addHeader("Product Search Results")
                    .startTable(new String[]{"Product ID", "Name", "Category", "Price", "Quantity"})
                    .addProductsToTable(products)
                    .endTable()
                    .build();

            sendResponse(exchange, 200, htmlPage, "text/html");
        } catch (Exception e) {
            sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage(), "Error");
        }
    }

    private List<Product> fetchProducts(Map<String, String> queryParams) {
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (key.equals("product_id")) {
                int productId = Integer.parseInt(value);
                Product product = dataAdapter.loadProduct(productId);
                return product != null ? List.of(product) : List.of();
            } else if (key.equals("price<")) {
                double maxPrice = Double.parseDouble(value);
                return dataAdapter.loadProductsByPriceRange(0, maxPrice);
            } else if (key.equals("price>")) {
                double minPrice = Double.parseDouble(value);
                return dataAdapter.loadProductsByPriceRange(minPrice, Double.MAX_VALUE);
            } else if (key.equals("price<=")) {
                double maxPrice = Double.parseDouble(value);
                return dataAdapter.loadProductsByPriceRange(0, maxPrice); // Inclusive range
            } else if (key.equals("price>=")) {
                double minPrice = Double.parseDouble(value);
                return dataAdapter.loadProductsByPriceRange(minPrice, Double.MAX_VALUE); // Inclusive range
            }
        }
        return List.of(); // Default to empty if no matching query parameter is found
    }


    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> queryParams = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return queryParams;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            // Check for valid operators in the key and extract the value
            String[] keyValue;
            if (pair.contains(">=")) {
                keyValue = pair.split(">=", 2);
                keyValue[0] += ">=";
            } else if (pair.contains("<=")) {
                keyValue = pair.split("<=", 2);
                keyValue[0] += "<=";
            } else if (pair.contains(">")) {
                keyValue = pair.split(">", 2);
                keyValue[0] += ">";
            } else if (pair.contains("<")) {
                keyValue = pair.split("<", 2);
                keyValue[0] += "<";
            } else if (pair.contains("=")) {
                keyValue = pair.split("=", 2);
                keyValue[0] += "=";
            } else {
                // Skip invalid pairs
                continue;
            }

            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                queryParams.put(key, value);
            }
        }
        return queryParams;
    }



    private void sendResponse(HttpExchange exchange, int statusCode, String response, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        byte[] responseBytes = response.getBytes();
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        exchange.getResponseBody().write(responseBytes);
        exchange.getResponseBody().close();
    }
}
