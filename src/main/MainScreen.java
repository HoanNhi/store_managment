package main;

import adapter.*;
import structure.Order;
import structure.Product;
import structure.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;



public class MainScreen extends JFrame {
    //Buttons for staff
    private final JButton btnReport = new JButton("Report");
    private final JButton btnSales = new JButton("Sales");
    private final JButton btnSupplier = new JButton("Supplier");
    private final JButton btnInventory = new JButton("Inventory");
    //Buttons for customer
    private final JButton btnBuy = new JButton("Buy");
    private final JButton btnEditInfo = new JButton("Edit Information");
    private final JButton btnViewOrderHistory = new JButton("View order history");
    //Buttons for all
    private final JButton btnLogOut = new JButton("Logout");
    private final JButton btnManageUsers = new JButton("Manage Users");
    private JLabel storeImageLabel; // For displaying the store image
    private JLabel welcomeLabel; // Welcome label
    DataAdapter dataAdapter;

    public MainScreen(DataAdapter dataAdapter, DataAdapterMongo dataAdapterMongo) {
        this.dataAdapter = dataAdapter;
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 500);

        // Left Panel for Buttons and Welcome Label
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setPreferredSize(new Dimension(200, getHeight()));
        buttonPanel.setBackground(Color.LIGHT_GRAY);

        // Bottom Panel for Buttons
        JPanel bottomButtonPanel = new JPanel();
        bottomButtonPanel.setLayout(new BoxLayout(bottomButtonPanel, BoxLayout.Y_AXIS));
        bottomButtonPanel.setBackground(Color.LIGHT_GRAY);

        User currentUser = Application.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Top Panel for Welcome Label
            welcomeLabel = new JLabel("Welcome user " + Application.getInstance().getCurrentUser().getUsername());
            welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Optional: Customize font
            buttonPanel.add(welcomeLabel, BorderLayout.NORTH);

            //Get role
            User.UserRole role = currentUser.getRole();

            if (role == User.UserRole.Manager) {
                bottomButtonPanel.add(createButtonPanel(btnInventory));
                bottomButtonPanel.add(createButtonPanel(btnSales));
                bottomButtonPanel.add(createButtonPanel(btnReport));
                bottomButtonPanel.add(createButtonPanel(btnSupplier));
                bottomButtonPanel.add(createButtonPanel(btnManageUsers));
            }
            else if (role == User.UserRole.Seller){
                bottomButtonPanel.add(createButtonPanel(btnInventory));
                bottomButtonPanel.add(createButtonPanel(btnSales));
            }
            else if (role == User.UserRole.Customer){
                bottomButtonPanel.add(createButtonPanel(btnBuy));
                bottomButtonPanel.add(createButtonPanel(btnViewOrderHistory));
                bottomButtonPanel.add(createButtonPanel(btnEditInfo));
            }
            bottomButtonPanel.add(createButtonPanel(btnLogOut)); // Logout for all roles
            buttonPanel.add(bottomButtonPanel, BorderLayout.SOUTH);

        } else {
            System.err.println("Error: No user logged in.");
        }

        // Center Panel for Store Image
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BorderLayout());
        storeImageLabel = new JLabel();
        storeImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        storeImageLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Load the image
        String imagePath = "src\\main\\store.png";
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            try {
                ImageIcon storeIcon = new ImageIcon(imageFile.getAbsolutePath());
                Image scaledImage = storeIcon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
                storeImageLabel.setIcon(new ImageIcon(scaledImage));
            } catch (Exception e) {
                storeImageLabel.setText("Error loading image: " + e.getMessage());
            }
        } else {
            storeImageLabel.setText("Store image not found: " + imagePath);
            System.out.println("Current working directory: " + System.getProperty("user.dir"));
        }
        imagePanel.add(storeImageLabel, BorderLayout.CENTER);

        // Add components to the frame
        this.add(buttonPanel, BorderLayout.WEST); // Left panel for buttons and label
        this.add(imagePanel, BorderLayout.CENTER); // Center panel for image

        btnReport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<Order> orders = dataAdapterMongo.loadAllOrders(); //Fetch orders
                if (!orders.isEmpty()) {
                    Application.getInstance().getOrderReportView().setOrders(orders); //Set fetched data
                    Application.getInstance().getOrderReportView().setVisible(true);
                }
                else{
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
                Application.getInstance().getSupplierView().setVisible(true); }
        });

        btnInventory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<Product> products = dataAdapter.loadAllProducts(); //Fetch products
                if (!products.isEmpty()) {
                    Application.getInstance().getProductDetailView().showProduct(products); //Set fetched data
                    Application.getInstance().getProductDetailView().setVisible(true);
                }
                else{
                    JOptionPane.showMessageDialog(MainScreen.this, "No Product Found");
                }
            }
        });

        btnLogOut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Application.getInstance().setCurrentUser(null);
                Application.getInstance().getMainScreen().setVisible(false);                      // Hide the MainScreen
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

    }

    // Helper method to wrap buttons with spacing
    private JPanel createButtonPanel(JButton button) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setMaximumSize(new Dimension(200, 50)); // Set a fixed height for buttons
        panel.add(button);
        return panel;
    }
}
