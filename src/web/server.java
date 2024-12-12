package web;

import adapter.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import structure.User;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.*;


public class server {

    private DataAdapterInterface dataAdapter; // Your data adapter


    public server(DataAdapterInterface adapter) {
        this.dataAdapter = adapter;

    }



    public void startServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Endpoint for login
        server.createContext("/login", new LoginHandler());


        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started on port " + port);


    }


    class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {


                    // Read request body (username and password)
                    String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    JSONObject json = new JSONObject(requestBody);
                    String username = json.getString("username");
                    String password = json.getString("password");

                    User user = dataAdapter.loadUser(username, password);

                    if (user != null) {


                        // Successful login
                        String response = "Login successful!";
                        exchange.sendResponseHeaders(200, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());

                    } else {

                        // Authentication failed
                        String response = "Login failed!";
                        exchange.sendResponseHeaders(401, response.length()); // 401 Unauthorized
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                    }

            }


            exchange.close();
        }
    }

    // Example usage (in your main Application class or elsewhere)
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:StoreDemo/store.db"); // Replace with your database URL


        DataAdapter dataAdapter = new DataAdapter(conn);
        server server = new server(dataAdapter);
        server.startServer(8000); // Start server on port 8000 (or any available port)

    }


}