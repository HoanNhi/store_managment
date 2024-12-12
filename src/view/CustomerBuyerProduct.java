package view;

import structure.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class CustomerBuyerProduct extends JFrame {
    private JTable productTable;
    private DefaultTableModel tableModel;
    private final String[] tableColumns = {"Product ID", "Product Name", "Category", "Supplier ID", "Unit Price", "Description", "Quantity"};
    private String[][] userData;

    public CustomerBuyerProduct() {
        // Window setup
        this.setTitle("Product Details");
        this.setSize(1028, 800);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());

        // Table setup
        tableModel = new DefaultTableModel(userData, tableColumns);
        productTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JScrollPane scrollPane = new JScrollPane(productTable);
        this.add(scrollPane, BorderLayout.CENTER);

        loadAndShowProducts();
    }

    public void showProduct(List<Product> products) {
        tableModel.setRowCount(0); // Clear existing rows
        for (Product product : products) {
            Object[] row = {
                    product.getProductID(),
                    product.getName(),
                    product.getCategory(),
                    product.getSupplierID(),
                    product.getPrice(),
                    product.getDescription(),
                    product.getQuantity()
            };
            tableModel.addRow(row);
        }
    }

    public void loadAndShowProducts() {
        try {
            // Make HTTP GET request to the API
            URL url = new URL("http://localhost:8000/inventory");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse JSON response into a list of products
                JSONArray jsonArray = new JSONArray(response.toString());
                List<Product> products = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonProduct = jsonArray.getJSONObject(i);
                    Product product = new Product();
                    product.setProductID(jsonProduct.getInt("productID"));
                    product.setName(jsonProduct.getString("name"));
                    product.setCategory(jsonProduct.getString("category"));
                    product.setSupplierID(jsonProduct.getInt("supplierID"));
                    product.setPrice(jsonProduct.getDouble("unitPrice"));
                    product.setDescription(jsonProduct.getString("description"));
                    product.setQuantity(jsonProduct.getInt("quantity"));
                    products.add(product);
                }

                // Display products in the table
                showProduct(products);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to load products. Response code: " + connection.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
