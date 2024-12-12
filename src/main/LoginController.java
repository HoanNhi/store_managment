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


import structure.User;
import view.LoginScreen;
import adapter.DataAdapter;

public class LoginController implements ActionListener, ItemListener { // Implement ItemListener
    private LoginScreen loginScreen;
    private DataAdapter dataAdapter;

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

            System.out.println("Login with username = " + username + " and password = " + password);
            User user = dataAdapter.loadUser(username, password);

            if (user == null) {
                JOptionPane.showMessageDialog(null, "This user does not exist!");
                loginScreen.setLoginSuccessful(false);
            }
            else {
                this.loginScreen.setVisible(false);

                if (user.getRole() == User.UserRole.Manager)
                    System.out.println("Manager");
                else if (user.getRole() == User.UserRole.Seller)
                    System.out.println("Seller");
                else
                    System.out.println("Customer");
                loginScreen.setLoginSuccessful(true);
                Application.getInstance().setCurrentUser(user);
                Application.getInstance().initializeAfterLogin(); // Initialize the rest of the application
                Application.getInstance().getMainScreen().setVisible(true);
                loginScreen.clearField();
            }
        }
    }

}