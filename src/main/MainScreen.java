package main;

import adapter.*;
import structure.Order;
import structure.Product;
import structure.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainScreen extends JFrame {
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

    public MainScreen(DataAdapter dataAdapter, DataAdapterMongo dataAdapterMongo) {
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

        User currentUser = Application.getInstance().getCurrentUser();
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
            User.UserRole role = currentUser.getRole();

            if (role == User.UserRole.Manager) {
                buttonPanel.add(styleButton(btnInventory));
                buttonPanel.add(styleButton(btnSales));
                buttonPanel.add(styleButton(btnReport));
                buttonPanel.add(styleButton(btnSupplier));
                buttonPanel.add(styleButton(btnShippers));
                buttonPanel.add(styleButton(btnManageUsers));
            } else if (role == User.UserRole.Seller) {
                buttonPanel.add(styleButton(btnInventory));
                buttonPanel.add(styleButton(btnSales));
            } else if (role == User.UserRole.Customer) {
                buttonPanel.add(styleButton(btnBuy));
                buttonPanel.add(styleButton(btnViewOrderHistory));
                buttonPanel.add(styleButton(btnEditInfo));
            }
            buttonPanel.add(styleButton(btnLogOut)); // Logout for all roles
        }

        // Add Panels to Frame
        mainPanel.add(welcomePanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        this.add(mainPanel);

        // Button Actions
        btnReport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<Order> orders = dataAdapterMongo.loadAllOrders(); // Fetch orders
                if (!orders.isEmpty()) {
                    Application.getInstance().getOrderReportView().setOrders(orders); // Set fetched data
                    Application.getInstance().getOrderReportView().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(MainScreen.this, "No Order found. Create one!");
                }
            }
        });

        btnSales.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Application.getInstance().getCheckoutScreen().setVisible(true);
            }
        });

        btnSupplier.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Application.getInstance().getSupplierView().setVisible(true);
            }
        });

        btnInventory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<Product> products = dataAdapter.loadAllProducts(); // Fetch products
                if (!products.isEmpty()) {
                    Application.getInstance().getProductDetailView().showProduct(products); // Set fetched data
                    Application.getInstance().getProductDetailView().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(MainScreen.this, "No Product Found");
                }
            }
        });

        btnLogOut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Application.getInstance().setCurrentUser(null);
                Application.getInstance().getMainScreen().setVisible(false); // Hide the MainScreen
                Application.getInstance().getLoginScreen().setVisible(true); // Show the LoginScreen
                Application.getInstance().getMainScreen().dispose();
            }
        });

        btnManageUsers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.getInstance().getManageUsersView().setVisible(true);
            }
        });

        btnEditInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.getInstance().getCustomerEditView().setVisible(true);
            }
        });

        btnBuy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.getInstance().getCustomerBuyerView().setVisible(true);
            }
        });

        btnViewOrderHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.getInstance().getOrderReportView().setVisible(true);
            }
        });

        btnShippers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.getInstance().getManageShippersView().setVisible(true);
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