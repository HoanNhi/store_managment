package main;

import adapter.DataAdapter;
import adapter.DataAdapterMongo;
import structure.Customer;
import structure.Order;
import structure.Product;
import structure.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CustomerMainScreen extends JFrame {
    // Buttons for staff
    private final JButton btnReport = new JButton("Report");
    private final JButton btnSales = new JButton("Sales");
    private final JButton btnSupplier = new JButton("Supplier");
    private final JButton btnInventory = new JButton("Inventory");
    private final JButton btnShippers = new JButton("Shippers");
    // Buttons for customer
    private final JButton btnBuy = new JButton("Buy");
    private final JButton btnEditInfo = new JButton("Edit Information");
    private final JButton btnViewOrderHistory = new JButton("View Order History");
    // Buttons for all
    private final JButton btnLogOut = new JButton("Logout");
    private final JButton btnManageUsers = new JButton("Manage Users");
    private JLabel welcomeLabel; // Welcome label
    DataAdapter dataAdapter;

    public CustomerMainScreen(DataAdapter dataAdapter, DataAdapterMongo dataAdapterMongo) {
        this.dataAdapter = dataAdapter;
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Welcome Panel
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        welcomePanel.setBackground(new Color(60, 179, 113));

        User currentUser = Customer_App.getInstance().getCurrentUser();
        if (currentUser != null) {
            welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername());
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
            welcomeLabel.setForeground(Color.WHITE);
            welcomePanel.add(welcomeLabel);
        } else {
            System.err.println("Error: No user logged in.");
        }

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1, 10, 10)); // Single column with spacing
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        if (currentUser != null) {
            buttonPanel.add(styleButton(btnBuy));
            buttonPanel.add(styleButton(btnViewOrderHistory));
            buttonPanel.add(styleButton(btnEditInfo));
            buttonPanel.add(styleButton(btnLogOut)); // Logout for all roles
        }

        // Add Panels to Frame
        mainPanel.add(welcomePanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        this.add(mainPanel);

        btnLogOut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Customer_App.getInstance().setCurrentUser(null);
                Customer_App.getInstance().setCustomerBuyerController(null);
                Customer_App.getInstance().setOrderReportController(null);
                Customer_App.getInstance().setCustomerEditController(null);
                Customer_App.getInstance().setOrderDetailView(null);
                Customer_App.getInstance().setOrderReportView(null);
                Customer_App.getInstance().setCustomerEditView(null);
                Customer_App.getInstance().getCustomerMainScreen().setVisible(false); // Hide the MainScreen
                Customer_App.getInstance().getLoginScreen().setVisible(true); // Show the LoginScreen
                Customer_App.getInstance().getCustomerMainScreen().dispose();
            }
        });

        btnEditInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Customer_App.getInstance().getCustomerEditView().setVisible(true);
            }
        });

        btnBuy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Customer_App.getInstance().getCustomerBuyerView().setVisible(true);
            }
        });

        btnViewOrderHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Customer_App.getInstance().getOrderReportView().setVisible(true);
            }
        });

    }

    private JPanel styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.add(button);
        panel.setBackground(new Color(245, 245, 245));
        return panel;
    }
}