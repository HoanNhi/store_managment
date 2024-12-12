package controller;

import main.Application;
import structure.Customer;
import structure.User;
import view.CustomerEditView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class CustomerEditController {

    private final CustomerEditView customerEditView;

    public CustomerEditController(CustomerEditView view) {
        this.customerEditView = view;

        customerEditView.getBtnSave().addActionListener(e -> saveCustomerChanges());
        customerEditView.getBtnCancel().addActionListener(e -> customerEditView.dispose());

        // Load customer information
        loadCustomerInformation();
    }

    private void saveCustomerChanges() {
        try {
            // Get updated customer and user information
            Customer updatedCustomer = getUpdatedCustomer();
            User updatedUser = getUpdatedUser();

            // Prepare JSON payload
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("customer", customerToJson(updatedCustomer));
            jsonPayload.put("user", userToJson(updatedUser));

            // Send HTTP PUT request to update customer information
            URL url = new URL("http://localhost:8000/editCustomer");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonPayload.toString().getBytes(StandardCharsets.UTF_8));
            }

            // Check response code
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(customerEditView, "Customer information updated successfully!");
            } else {
                JOptionPane.showMessageDialog(customerEditView, "Error updating customer information. Response code: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(customerEditView, "Error: " + e.getMessage());
        }
    }

    private void loadCustomerInformation() {
        try {
            int customerID = Application.getInstance().getCurrentUser().getUserID();

            // Send HTTP GET request to retrieve customer information
            URL url = new URL("http://localhost:8000/editCustomer?customerID=" + customerID);
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

                // Parse the JSON response
                JSONObject responseJson = new JSONObject(response.toString());
                JSONObject customerJson = responseJson.getJSONObject("customer");
                JSONObject userJson = responseJson.getJSONObject("user");

                Customer customer = jsonToCustomer(customerJson);
                User user = jsonToUser(userJson);

                // Set customer and user information in the view
                customerEditView.setCustomer(customer);
                customerEditView.setUser(user);
            } else {
                JOptionPane.showMessageDialog(customerEditView, "Error loading customer information. Response code: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(customerEditView, "Error: " + e.getMessage());
        }
    }

    private User getUpdatedUser() {
        User user = Application.getInstance().getCurrentUser();
        return user;
    }

    private Customer getUpdatedCustomer() {
        Customer customer = customerEditView.getCustomer();
        customer.setFirstName(customerEditView.getTxtFirstName().getText());
        customer.setLastName(customerEditView.getTxtLastName().getText());
        customer.setAddress(customerEditView.getTxtAddress().getText());
        customer.setEmail(customerEditView.getTxtEmail().getText());
        customer.setPhone(customerEditView.getTxtPhone().getText());
        customer.setCard(
                customerEditView.getTxtCardNumber().getText(),
                customerEditView.getTxtCSV().getText(),
                customerEditView.getTxtExpiryYear().getText(),
                customerEditView.getTxtExpiryMonth().getText(),
                customerEditView.getTxtCardType()
        );
        return customer;
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
        return user;
    }
}
