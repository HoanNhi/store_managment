// CustomerDialog.java (New file - create this)
package view;

import structure.Customer;
import structure.Shipper;
import adapter.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.lang.String;

public class CustomerView extends JDialog {
    private final JTextField txtCustomerID = new JTextField(10);
    private final JTextField txtFirstName = new JTextField(20);
    private final JTextField txtLastName = new JTextField(20);
    private final JTextField txtEmail = new JTextField(30);
    private final JTextField txtPhone = new JTextField(20);
    private final JTextField txtAddress = new JTextField(50);
    private final JComboBox<String> userAccessLevelComboBox;
    private final Dimension inputBoxDimension = new Dimension(300, 20);

    private final JButton btnLoad = new JButton("LoadCustomer");
    private final JButton btnOK = new JButton("OK");
    private final JButton btnCancel = new JButton("Cancel");

    private DataAdapterInterface dataAdapterMongo;
    private Customer customer = null;

    public CustomerView(JFrame parent, DataAdapterInterface dataAdapterMongo) {
        super(parent, "Customer Information", true); // Modal dialog
        this.dataAdapterMongo = dataAdapterMongo;
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());
        JPanel panelFields = new JPanel(new GridLayout(7, 2));

        panelFields.add(new JLabel("CustomerID:"));
        panelFields.add(txtCustomerID);
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

        ArrayList<String> shipperNames = new ArrayList<>();
        shipperNames.add(null);
        shipperNames.addAll(dataAdapterMongo.loadAllShippers().stream()
                .map(Shipper::getShipperName)
                .collect(Collectors.toCollection(ArrayList::new))); // Use the diamond operator <>

        userAccessLevelComboBox = new JComboBox<>(shipperNames.toArray(new String[shipperNames.size()]));
        userAccessLevelComboBox.setPreferredSize(inputBoxDimension);
        userAccessLevelComboBox.setFocusable(false);
        panelFields.add(new JLabel("Shippers:"));
        panelFields.add(userAccessLevelComboBox);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center align buttons in panelButtons
        panelButtons.add(btnOK);
        panelButtons.add(btnCancel);

        JPanel panelLoadBut = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Use BorderLayout for panelLoadBut
        panelLoadBut.add(btnLoad, BorderLayout.NORTH); // Add btnLoad to the top/north


        JPanel combinedButtonPanel = new JPanel(new BorderLayout()); // New panel to combine button panels
        combinedButtonPanel.add(panelLoadBut, BorderLayout.NORTH);  // Load button at the top
        combinedButtonPanel.add(panelButtons, BorderLayout.SOUTH); // OK/Cancel buttons at the bottom



        add(panelFields, BorderLayout.NORTH);
        add(combinedButtonPanel, BorderLayout.SOUTH); // Add the combined button panel to the south


        pack();
        setLocationRelativeTo(parent);

        btnLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int customerId;
                try {
                    customerId = Integer.parseInt(txtCustomerID.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(CustomerView.this, "Invalid customer ID format.");
                    return;
                }

                customer = dataAdapterMongo.loadCustomer(customerId);

                if (customer == null) { // Create new customer if not found
                    JOptionPane.showMessageDialog(CustomerView.this, "No customer found!!!"); // Show error message
                    return; // Stop further processing
                }
                else{
                    txtFirstName.setText(customer.getFirstName());
                    txtLastName.setText(customer.getLastName());
                    txtEmail.setText(customer.getEmail());
                    txtPhone.setText(customer.getPhone());
                    txtAddress.setText(customer.getAddress());
                }
            }
        });

        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int customerId;
                try {
                    customerId = Integer.parseInt(txtCustomerID.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(CustomerView.this, "Invalid customer ID format.");
                    return;
                }

                customer = dataAdapterMongo.loadCustomer(customerId);

                if (customer == null) { // Create new customer if not found
                    JOptionPane.showMessageDialog(CustomerView.this, "No customer found!!!"); // Show error message
                    return; // Stop further processing
                }

                setVisible(false); // Close the dialog after loading or creating

            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customer = null; //Ensure no customer is returned in case of cancel
                setVisible(false);

            }
        });

    }

    public Customer getCustomer() {
        return customer; // Returns the loaded or newly created customer
    }

    public String getAddress() {
        return txtAddress.getText();
    }

    public String getShipperName() {
        return userAccessLevelComboBox.getSelectedItem().toString();
    }
}