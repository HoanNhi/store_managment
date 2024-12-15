package controller;

import org.json.JSONArray;
import org.json.JSONObject;
import view.ManageShippersView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ManageShippersController implements ActionListener {

    private ManageShippersView manageShippersView;

    public ManageShippersController(ManageShippersView manageShippersView) {
        this.manageShippersView = manageShippersView;

        manageShippersView.getCreateShipperButton().addActionListener(this);
        manageShippersView.getUpdateShipperButton().addActionListener(this);
        manageShippersView.getDeleteShipperButton().addActionListener(this);

        loadAllShippers();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(manageShippersView.getCreateShipperButton())) {
            createShipper();
            manageShippersView.clearInputBoxes();
            loadAllShippers();
        } else if (e.getSource().equals(manageShippersView.getUpdateShipperButton())) {
            updateShipper();
            manageShippersView.clearInputBoxes();
            loadAllShippers();
        } else if (e.getSource().equals(manageShippersView.getDeleteShipperButton())) {
            deleteShipper();
            manageShippersView.clearInputBoxes();
            loadAllShippers();
        }
    }

    private void loadAllShippers() {
        try {
            URL url = new URL("http://localhost:8000/shippers");
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

                JSONArray shippersArray = new JSONArray(response.toString());
                String[][] shipperData = new String[shippersArray.length()][3]; // Adjust for shipper fields

                for (int i = 0; i < shippersArray.length(); i++) {
                    JSONObject shipperJson = shippersArray.getJSONObject(i);
                    shipperData[i][0] = String.valueOf(shipperJson.getInt("shipperID"));
                    shipperData[i][1] = shipperJson.getString("companyName");
                    shipperData[i][2] = String.valueOf(shipperJson.getDouble("pricePerKM"));
                }

                manageShippersView.setShipperDataTable(shipperData);
                manageShippersView.updateTable();
            } else {
                JOptionPane.showMessageDialog(manageShippersView, "Failed to load shippers. Response code: " + connection.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(manageShippersView, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createShipper() {
        try {
            JSONObject shipperPayload = manageShippersView.getShipperPayload();
            URL url = new URL("http://localhost:8000/shippers");
            HttpURLConnection connection = createConnection(url, "POST");

            writePayload(connection, shipperPayload);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                JOptionPane.showMessageDialog(manageShippersView, "Shipper created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(manageShippersView, "Failed to create shipper. Response code: " + connection.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(manageShippersView, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateShipper() {
        try {
            JSONObject shipperPayload = manageShippersView.getShipperPayload();
            URL url = new URL("http://localhost:8000/shippers");
            HttpURLConnection connection = createConnection(url, "PUT");

            writePayload(connection, shipperPayload);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(manageShippersView, "Shipper updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(manageShippersView, "Failed to update shipper. Response code: " + connection.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(manageShippersView, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteShipper() {
        try {
            int shipperID = Integer.parseInt(manageShippersView.getShipperIDTextField().getText());
            URL url = new URL("http://localhost:8000/shippers?shipperID=" + shipperID);
            HttpURLConnection connection = createConnection(url, "DELETE");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(manageShippersView, "Shipper deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(manageShippersView, "Failed to delete shipper. Response code: " + connection.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(manageShippersView, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private HttpURLConnection createConnection(URL url, String method) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        return connection;
    }

    private void writePayload(HttpURLConnection connection, JSONObject payload) throws Exception {
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
    }
}
