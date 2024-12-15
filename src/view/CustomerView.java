// CustomerDialog.java (Adapted for API)
package view;

import structure.Customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

public class CustomerView extends JDialog {
    private final JComboBox<String> customerComboBox;
    private final JTextField txtFirstName = new JTextField(20);
    private final JTextField txtLastName = new JTextField(20);
    private final JTextField txtEmail = new JTextField(30);
    private final JTextField txtPhone = new JTextField(20);
    private final JTextField txtAddress = new JTextField(50);
    private final JComboBox<String> shipperCompanySelection;
    private final Dimension inputBoxDimension = new Dimension(300, 20);

    private final JButton btnOK = new JButton("OK");
    private final JButton btnCancel = new JButton("Cancel");

    private Customer customer = null;

    public CustomerView(JFrame parent) {
        super(parent, "Customer Information", true); // Modal dialog
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());
        JPanel panelFields = new JPanel(new GridLayout(7, 2));

        panelFields.add(new JLabel("Customer:"));
        // Load the customers
        ArrayList<Customer> customers = loadCustomersFromAPI();


        Map<String, Customer> customerMap = customers.stream()
                .collect(Collectors.toMap(customer -> String.valueOf(customer.getCustomerID()), customer -> customer));


        customerComboBox = new JComboBox<>(customerMap.keySet().toArray(new String[0]));
        customerComboBox.setPreferredSize(inputBoxDimension);


        customerComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedID = (String) customerComboBox.getSelectedItem();
                Customer selectedCustomer = customerMap.get(selectedID);

                if (selectedCustomer != null) {
                    txtFirstName.setText(selectedCustomer.getFirstName());
                    txtLastName.setText(selectedCustomer.getLastName());
                    txtEmail.setText(selectedCustomer.getEmail());
                    txtPhone.setText(selectedCustomer.getPhone());
                    txtAddress.setText(selectedCustomer.getAddress());
                }
            }
        });
        panelFields.add(customerComboBox);

        panelFields.add(new JLabel("First Name:"));
        panelFields.add(txtFirstName);
        panelFields.add(new JLabel("Last Name:"));
        panelFields.add(txtLastName);
        panelFields.add(new JLabel("Email:"));
        panelFields.add(txtEmail);
        panelFields.add(new JLabel("Phone:"));
        panelFields.add(txtPhone);
        panelFields.add(new JLabel("Address:"));
        panelFields.add(txtAddress);

        ArrayList<String> shipperNames = loadShippersFromAPI();
        shipperCompanySelection = new JComboBox<>(shipperNames.toArray(new String[0]));
        shipperCompanySelection.setPreferredSize(inputBoxDimension);
        shipperCompanySelection.setFocusable(false);
        panelFields.add(new JLabel("Shippers:"));
        panelFields.add(shipperCompanySelection);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelButtons.add(btnOK);
        panelButtons.add(btnCancel);

        add(panelFields, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);

        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Customer selectedCustomer = customerMap.get((String) customerComboBox.getSelectedItem());
                if (selectedCustomer == null) {
                    JOptionPane.showMessageDialog(CustomerView.this, "No customer selected.");
                    return;
                }
                customer = selectedCustomer;
                setVisible(false);
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customer = null;
                setVisible(false);
            }
        });
    }

    private ArrayList<Customer> loadCustomersFromAPI() {
        ArrayList<Customer> customers = new ArrayList<>();
        try {
            URL url = new URL("http://localhost:8000/customer");
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

                JSONArray customerArray = new JSONArray(response.toString());
                for (int i = 0; i < customerArray.length(); i++) {
                    JSONObject customerJson = customerArray.getJSONObject(i);
                    Customer customer = new Customer();
                    customer.setCustomerID(customerJson.getInt("customerID"));
                    customer.setFirstName(customerJson.getString("firstName"));
                    customer.setLastName(customerJson.getString("lastName"));
                    customer.setEmail(customerJson.getString("email"));
                    customer.setPhone(customerJson.getString("phone"));
                    customer.setAddress(customerJson.getString("address"));
                    customers.add(customer);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading customers: " + e.getMessage());
        }
        return customers;
    }

    private ArrayList<String> loadShippersFromAPI() {
        ArrayList<String> shippers = new ArrayList<>();
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

                JSONArray shipperArray = new JSONArray(response.toString());
                for (int i = 0; i < shipperArray.length(); i++) {
                    JSONObject shipper = shipperArray.getJSONObject(i);
                    shippers.add(shipper.getString("companyName"));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading shippers: " + e.getMessage());
        }
        return shippers;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getAddress() {
        return txtAddress.getText();
    }

    public String getCompanyName() {
        return shipperCompanySelection.getSelectedItem().toString();
    }
}
