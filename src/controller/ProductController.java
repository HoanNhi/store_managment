package controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import adapter.DataAdapter;
import structure.Product;
import view.ProductDetailView;

public class ProductController implements ActionListener {
    private ProductDetailView productView;
    private DataAdapter dataAdapter; // to save and load product information
    private String apiEndpoint = "http://localhost:8000/inventory";

    public ProductController(ProductDetailView productView, DataAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
        this.productView = productView;

        productView.getBtnCreate().addActionListener(this);
        productView.getBtnUpdate().addActionListener(this);
        productView.getBtnDelete().addActionListener(this); // Add ActionListener to the delete button
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == productView.getBtnCreate()) {
            saveProduct();
        } else if (e.getSource() == productView.getBtnUpdate()) {
            updateProduct();
        } else if (e.getSource() == productView.getBtnDelete()) { // Handle Delete button click
            deleteProduct();
        }
    }

    private void saveProduct() {
        try {
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("productID", Integer.parseInt(productView.getTxtProductID().getText()));
            jsonPayload.put("name", productView.getTxtProductName().getText());
            jsonPayload.put("category", productView.getTxtProductCategory().getText());
            jsonPayload.put("supplierID", Integer.parseInt(productView.getTxtProductSupplierID().getText()));
            jsonPayload.put("unitPrice", Double.parseDouble(productView.getTxtProductPrice().getText()));
            jsonPayload.put("description", productView.getTxtProductDescription().getText());
            jsonPayload.put("quantity", Integer.parseInt(productView.getTxtProductQuantity().getText()));

            sendRequest("POST", jsonPayload.toString());
            productView.clearProductInputBoxes();

            JOptionPane.showMessageDialog(null, "Product saved successfully!");
            loadProductsFromAPI();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private void updateProduct() {
        try {
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("productID", Integer.parseInt(productView.getTxtProductID().getText()));
            jsonPayload.put("name", productView.getTxtProductName().getText());
            jsonPayload.put("category", productView.getTxtProductCategory().getText());
            jsonPayload.put("supplierID", Integer.parseInt(productView.getTxtProductSupplierID().getText()));
            jsonPayload.put("unitPrice", Double.parseDouble(productView.getTxtProductPrice().getText()));
            jsonPayload.put("description", productView.getTxtProductDescription().getText());
            jsonPayload.put("quantity", Integer.parseInt(productView.getTxtProductQuantity().getText()));

            sendRequest("PUT", jsonPayload.toString());
            JOptionPane.showMessageDialog(null, "Product updated successfully!");
            loadProductsFromAPI();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private void deleteProduct() {
        try {
            int productID = Integer.parseInt(productView.getTxtProductID().getText());
            URL url = new URL(apiEndpoint + "?productID=" + productID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                JOptionPane.showMessageDialog(null, "Product deleted successfully!");
                loadProductsFromAPI();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete product.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private void sendRequest(String method, String payload) throws IOException {
        URL url = new URL(apiEndpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
            throw new IOException("Failed with response code: " + responseCode);
        }
    }

    private void loadProductsFromAPI() {  // New method to load products from API
        try {
            URL url = new URL(apiEndpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json"); // Expect JSON response

            int responseCode = connection.getResponseCode();


            if (responseCode == HttpURLConnection.HTTP_OK) {


                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String responseLine;
                    StringBuilder response = new StringBuilder();
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    //Parse JSON and update table
                    JSONArray jsonArray = new JSONArray(response.toString());
                    List<Product> productList = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        Product product = new Product();
                        product.setProductID(obj.getInt("productID"));
                        product.setName(obj.getString("name"));
                        product.setCategory(obj.getString("category"));
                        product.setSupplierID(obj.getInt("supplierID"));
                        product.setPrice(obj.getDouble("unitPrice"));
                        product.setDescription(obj.getString("description"));
                        product.setQuantity(obj.getInt("quantity"));
                        productList.add(product);

                    }

                    productView.showProduct(productList);

                }


            } else {
                JOptionPane.showMessageDialog(null, "Failed to load products from server. Response code: " + responseCode);
            }

            connection.disconnect(); // Close the connection

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error communicating with the server: " + e.getMessage());
            e.printStackTrace();
        }

    }

}