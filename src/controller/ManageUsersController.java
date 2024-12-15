package controller;

import org.json.JSONArray;
import org.json.JSONObject;
import view.ManageUsersView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ManageUsersController implements ActionListener {

    private ManageUsersView manageUsersView;

    public ManageUsersController(ManageUsersView manageUsersView) {
        this.manageUsersView = manageUsersView;

        manageUsersView.getCreateUserButton().addActionListener(this);
        manageUsersView.getDeleteUserButton().addActionListener(this);
        manageUsersView.getUpdateUserButton().addActionListener(this);

        loadAllUsers();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(manageUsersView.getCreateUserButton())) {
            createUser();
            manageUsersView.clearInputBoxes();
            loadAllUsers();
        } else if (e.getSource().equals(manageUsersView.getDeleteUserButton())) {
            deleteUser();
            manageUsersView.clearInputBoxes();
            loadAllUsers();
        } else if (e.getSource().equals(manageUsersView.getUpdateUserButton())) {
            updateUser();
            manageUsersView.clearInputBoxes();
            loadAllUsers();
        }
    }

    public void loadAllUsers() {
        try {
            URL url = new URL("http://localhost:8000/users");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Parse JSON array
                    JSONArray usersArray = new JSONArray(response.toString());
                    String[][] userData = new String[usersArray.length()][9]; // Adjust columns if needed

                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject userJson = usersArray.getJSONObject(i);
                        userData[i][0] = String.valueOf(userJson.getInt("userID"));
                        userData[i][1] = userJson.getString("username");
                        userData[i][2] = userJson.optString("password", "");
                        userData[i][3] = userJson.getString("firstName");
                        userData[i][4] = userJson.getString("lastName");
                        userData[i][5] = userJson.getString("email");
                        userData[i][6] = userJson.getString("phone");
                        userData[i][7] = userJson.getString("address");
                        userData[i][8] = userJson.getString("role");
                    }

                    manageUsersView.setUserDataTable(userData);
                    manageUsersView.updateTable();
                }
            } else {
                JOptionPane.showMessageDialog(manageUsersView, "Failed to load users. Response code: " + connection.getResponseCode(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(manageUsersView, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void createUser() {
        if (manageUsersView.areBoxesEmpty()) {
            JOptionPane.showMessageDialog(manageUsersView, "One of the fields is empty! Please try again!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            JSONObject jsonPayload = getUserPayload();
            URL url = new URL("http://localhost:8000/users");
            HttpURLConnection connection = createConnection(url, "POST");

            writePayload(connection, jsonPayload);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                JOptionPane.showMessageDialog(manageUsersView, "User created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(manageUsersView, "Failed to create user. Response code: " + responseCode, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(manageUsersView, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateUser() {
        if (manageUsersView.areBoxesEmpty()) {
            JOptionPane.showMessageDialog(manageUsersView, "One of the fields is empty! Please try again!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            JSONObject jsonPayload = getUserPayload();
            URL url = new URL("http://localhost:8000/users");
            HttpURLConnection connection = createConnection(url, "PUT");

            writePayload(connection, jsonPayload);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(manageUsersView, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(manageUsersView, "Failed to update user. Response code: " + responseCode, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(manageUsersView, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteUser() {
        try {
            int userID = Integer.parseInt(manageUsersView.getUserIDTextField().getText());
            URL url = new URL("http://localhost:8000/users?userID=" + userID);
            HttpURLConnection connection = createConnection(url, "DELETE");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(manageUsersView, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(manageUsersView, "Failed to delete user. Response code: " + responseCode, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(manageUsersView, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JSONObject getUserPayload() {
        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("userID", Integer.parseInt(manageUsersView.getUserIDTextField().getText()));
        jsonPayload.put("username", manageUsersView.getUsernameTextField().getText());
        jsonPayload.put("password", manageUsersView.getPasswordTextField().getText());
        jsonPayload.put("firstName", manageUsersView.getFirstNameTextField().getText());
        jsonPayload.put("lastName", manageUsersView.getLastNameTextField().getText());
        jsonPayload.put("email", manageUsersView.getEmailTextField().getText());
        jsonPayload.put("phone", manageUsersView.getPhoneTextField().getText());
        jsonPayload.put("address", manageUsersView.getAddressTextField().getText());
        jsonPayload.put("role", manageUsersView.getUserRole());
        return jsonPayload;
    }

    private HttpURLConnection createConnection(URL url, String method) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setDoOutput(true);
        return connection;
    }

    private void writePayload(HttpURLConnection connection, JSONObject payload) throws IOException {
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
    }
}
