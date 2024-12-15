package main;

import adapter.DataAdapter;
import structure.User;
import view.LoginScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CustomerLoginController implements ActionListener, ItemListener { // Implement ItemListener
    private LoginScreen loginScreen;
    private DataAdapter dataAdapter;
    private String apiEndpoint = "http://localhost:8000/login"; // Your API endpoint

    public CustomerLoginController(LoginScreen loginScreen, DataAdapter dataAdapter) {
        this.loginScreen = loginScreen;
        this.dataAdapter = dataAdapter;

        // Add listeners to the components in the LoginScreen
        this.loginScreen.getBtnLogin().addActionListener(this);
        this.loginScreen.showPasswordCheckBox.addItemListener(this); // Add ItemListener
        addLoginButtonMouseListener();
    }


    private void addLoginButtonMouseListener() {
        this.loginScreen.getBtnLogin().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginScreen.getBtnLogin().setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                loginScreen.getBtnLogin().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

    }

    @Override
    public void itemStateChanged(ItemEvent e) {  // ItemListener method
        if (e.getSource() == loginScreen.showPasswordCheckBox) {
            loginScreen.checkShowPasswordBox(); // Call checkShowPasswordBox in LoginScreen
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginScreen.getBtnLogin()) {
            String username = loginScreen.getTxtUserName().getText().trim();
            String password = loginScreen.getPassword();
            try {
                String query = String.format("username=%s&password=%s",
                        URLEncoder.encode(username, StandardCharsets.UTF_8),
                        URLEncoder.encode(password, StandardCharsets.UTF_8));
                URL url = new URL(apiEndpoint + "?" + query); // Add parameters to the URL as query string
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    StringBuilder response = new StringBuilder();
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                    }

                    //4. Load user data using DataAdapter from database
                    User authenticatedUser = dataAdapter.loadUser(username, password);

                    if (authenticatedUser != null) {
                        Customer_App.getInstance().setCurrentUser(authenticatedUser);
                        Customer_App.getInstance().initializeAfterLogin(); // Initialize after login
                        Customer_App.getInstance().getCustomerMainScreen().setVisible(true);
                        loginScreen.setVisible(false);  // Hide login screen
                        loginScreen.clearField(); // Clear after successful login
                    } else {
                        JOptionPane.showMessageDialog(null, "Authentication successful, but user data not found in the database.");
                    }


                } else {
                    JOptionPane.showMessageDialog(null, "Login failed. Please check your username and password.");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error communicating with the server: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

}