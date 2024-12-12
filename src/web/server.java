package web;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.sun.net.httpserver.HttpServer;
import web.SearchHandler;

import adapter.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executors;

public class server {
    public static void main(String[] args) {
        try {
            // Define the server port
            int port = 8000;
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            /**************************************SQLITE**************************************/

            String url = "jdbc:sqlite:StoreDemo/store.db";

            Connection connection = DriverManager.getConnection(url);
            DataAdapterInterface dataAdapterSQL = new DataAdapter(connection);

            /**************************************SQLITE**************************************/
            /**************************************MongoDB**************************************/

            String uri = "mongodb://localhost:27017";  // Connection string for local MongoDB
            MongoClient mongoClient = MongoClients.create(uri);
            MongoDatabase database = mongoClient.getDatabase("storeManagement"); // Connect to "storeManagement" database
            DataAdapterInterface dataAdapterMongo = new DataAdapterMongo(database);

            /**************************************MongoDB**************************************/

            // Create context for /search endpoint
            server.createContext("/search", new ProductSearchAPI(dataAdapterSQL));

            server.createContext("/login", new UserQuery(dataAdapterSQL));

            server.createContext("/orderShop", new orderFromShop(dataAdapterMongo, dataAdapterSQL));

            server.createContext("/orderCustomer", new orderFromCustomer(dataAdapterMongo, dataAdapterSQL));

            server.createContext("/users", new UserManagement(dataAdapterSQL, dataAdapterMongo));

            server.createContext("/suppliers", new SupplierQuery(dataAdapterSQL));

            server.createContext("/inventory", new InventoryManagement(dataAdapterSQL));

            server.createContext("/sales", new SaleManagement(dataAdapterMongo));

            server.createContext("/editCustomer", new CustomerEditAPI(dataAdapterMongo, dataAdapterSQL));

            // Optionally, create context for other endpoints like /register, /sales-report, etc.

            // Set executor to handle multiple requests concurrently
            server.setExecutor(Executors.newFixedThreadPool(10));

            // Start the server
            server.start();
            System.out.println("Server started on port " + port);

            // Add shutdown hook for graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down server...");
                server.stop(1);
//                DataStore.closeMongoConnection(); // Close MongoDB connection if necessary
                System.out.println("Server stopped.");
            }));

        } catch (IOException e) {
            System.out.println("Failed to create HTTP server on port " + 8000);
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}