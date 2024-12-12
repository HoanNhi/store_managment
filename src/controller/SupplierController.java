package controller;

import org.json.JSONObject;
import org.json.JSONArray;
import view.SupplierView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SupplierController implements ActionListener {
    private SupplierView supplierView;

    public SupplierController(SupplierView view) {
        this.supplierView = view;

        view.getBtnLoad().addActionListener(this);
        view.getBtnSave().addActionListener(this);
        view.getBtnDelete().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == supplierView.getBtnLoad()) {
            try {
                int supplierID = Integer.parseInt(supplierView.getTxtSupplierID().getText());
                loadSupplier(supplierID);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(supplierView, "Supplier ID invalid. Please try again");
            }
        } else if (e.getSource() == supplierView.getBtnSave()) {
            saveSupplier();
        } else if (e.getSource() == supplierView.getBtnDelete()) {
            deleteSupplier();
        }
    }

    private void loadSupplier(int supplierID) {
        try {
            URL url = new URL("http://localhost:8000/suppliers?supplierID=" + supplierID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                JSONObject supplierJson = new JSONObject(reader.readLine());
                supplierView.getTxtSupplierName().setText(supplierJson.getString("name"));
                supplierView.getTxtContactPerson().setText(supplierJson.getString("contactPerson"));
                supplierView.getTxtPhone().setText(supplierJson.getString("phone"));
            } else {
                JOptionPane.showMessageDialog(supplierView, "Supplier not found.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(supplierView, "Error: " + e.getMessage());
        }
    }

    private void saveSupplier() {
        try {
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("supplierID", Integer.parseInt(supplierView.getTxtSupplierID().getText()));
            jsonPayload.put("name", supplierView.getTxtSupplierName().getText());
            jsonPayload.put("contactPerson", supplierView.getTxtContactPerson().getText());
            jsonPayload.put("phone", supplierView.getTxtPhone().getText());

            URL url = new URL("http://localhost:8000/suppliers");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonPayload.toString().getBytes());
            }

            if (connection.getResponseCode() == 200) {
                JOptionPane.showMessageDialog(supplierView, "Supplier saved successfully.");
            } else {
                JOptionPane.showMessageDialog(supplierView, "Failed to save supplier.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(supplierView, "Error: " + e.getMessage());
        }
    }

    private void deleteSupplier() {
        try {
            int supplierID = Integer.parseInt(supplierView.getTxtSupplierID().getText());
            URL url = new URL("http://localhost:8000/suppliers?supplierID=" + supplierID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            if (connection.getResponseCode() == 200) {
                JOptionPane.showMessageDialog(supplierView, "Supplier deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(supplierView, "Failed to delete supplier.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(supplierView, "Error: " + e.getMessage());
        }
    }
}
