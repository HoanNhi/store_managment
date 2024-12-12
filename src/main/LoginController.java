package main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.Cursor;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


import structure.User;
import view.LoginScreen;
import adapter.DataAdapter;

public class LoginController implements ActionListener, ItemListener { // Implement ItemListener
    private LoginScreen loginScreen;
    private DataAdapter dataAdapter;
    private String apiEndpoint = "http://localhost:8000/login"; // Your API endpoint

    public LoginController(LoginScreen loginScreen, DataAdapter dataAdapter) {
        this.loginScreen = loginScreen;
        this.dataAdapter = dataAdapter;

        // Add listeners to the components in the LoginScreen
        this.loginScreen.getBtnLogin().addActionListener(this);
        this.loginScreen.showPasswordCheckBox.addItemListener(this); // Add ItemListener
        addEyeIconMouseListener();
        addLoginButtonMouseListener();
    }


    private void addEyeIconMouseListener() {
        this.loginScreen.eyeIconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                loginScreen.togglePasswordVisibility();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                loginScreen.eyeIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginScreen.eyeIconLabel.setCursor(Cursor.getDefaultCursor());
            }
        });
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

//            try {
//                // 1. Create JSON payload
//                JSONObject jsonPayload = new JSONObject();
//                jsonPayload.put("username", username);
//                jsonPayload.put("password", password);
//
//                URL url = new URL(apiEndpoint);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//                connection.setRequestProperty("Content-Type", "application/json; utf-8");
//                connection.setRequestProperty("Accept", "application/json"); // For receiving JSON response
//                connection.setDoOutput(false); // For sending request body
//
//                try (OutputStream os = connection.getOutputStream()) {
//                    byte[] input = jsonPayload.toString().getBytes(StandardCharsets.UTF_8);
//                    os.write(input, 0, input.length);
//                }
//
//                int responseCode = connection.getResponseCode();
//
//
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    // 3. Read JSON response (if any)
//                    StringBuilder response = new StringBuilder();
//                    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
//                        String responseLine;
//                        while ((responseLine = br.readLine()) != null) {
//                            response.append(responseLine.trim());
//                        }
//                    }
//                    JSONObject jsonResponse = new JSONObject(response.toString()); //Parse the response
//
//
//                    //4. Load user data using DataAdapter from database
//                    User authenticatedUser = dataAdapter.loadUser(username, password);
//
//                    if (authenticatedUser != null) {
//                        Application.getInstance().setCurrentUser(authenticatedUser);
//                        Application.getInstance().initializeAfterLogin(); // Initialize after login
//                        Application.getInstance().getMainScreen().setVisible(true);
//                        loginScreen.setVisible(false);  // Hide login screen
//                        loginScreen.clearField(); // Clear after successful login
//                    } else {
//                        JOptionPane.showMessageDialog(null, "Authentication successful, but user data not found in the database.");
//                    }
//
//
//
//                } else {
//                    JOptionPane.showMessageDialog(null, "Login failed. Please check your username and password."); // Or display error message
//                }
//
//            } catch (IOException ex) {
//                JOptionPane.showMessageDialog(null, "Error communicating with the server: " + ex.getMessage());
//                ex.printStackTrace(); // Or log the error
//            }

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
                        Application.getInstance().setCurrentUser(authenticatedUser);
                        Application.getInstance().initializeAfterLogin(); // Initialize after login
                        Application.getInstance().getMainScreen().setVisible(true);
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