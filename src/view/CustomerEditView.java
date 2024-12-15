// CustomerEditView.java (View)
package view;

import main.Application;
import structure.Customer;
import structure.User;

import javax.swing.*;
import java.awt.*;

public class CustomerEditView extends JDialog {
    private JTextField txtFirstName = new JTextField(20);
    private JTextField txtLastName = new JTextField(20);
    private JTextField txtPhone = new JTextField(20);
    private JTextField txtEmail = new JTextField(30);
    private JTextField txtAddress = new JTextField(50);
    private JTextField txtCardNumber = new JTextField(20);
    private JTextField txtExpiryYear = new JTextField(4);
    private JTextField txtExpiryMonth = new JTextField(10);
    private JTextField txtCSV = new JTextField(15);
    private JTextField txtUsername = new JTextField(20);
    private JPasswordField txtPassword = new JPasswordField(20);
    private JComboBox<String> txtCardType = new JComboBox<>(new String[] {"Credit card", "Debit card", "American Express", "Visa", "MasterCard"});



    private JButton btnSave = new JButton("Save Changes");
    private JButton btnCancel = new JButton("Cancel");

    private Customer customer;
    private User user;


    public CustomerEditView(JFrame parent, Customer customer, User user) {
        super(parent, "Edit Customer Information", true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.user = user;
        this.customer = customer;
        this.txtUsername.setEnabled(false);

        setLayout(new BorderLayout());
        JPanel panelFields = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.WEST; // Align labels to the left/west
        gc.insets = new Insets(5, 5, 5, 5);  // Add some padding


        // Add input fields with labels (using GridBagLayout for better alignment)
        addInputComponent(panelFields, gc, "First Name:", txtFirstName);
        addInputComponent(panelFields, gc, "Last Name:", txtLastName);
        addInputComponent(panelFields, gc, "Phone:", txtPhone);
        addInputComponent(panelFields, gc, "Email:", txtEmail);
        addInputComponent(panelFields, gc, "Address:", txtAddress);
        addInputComponent(panelFields, gc, "Username:", txtUsername);
        addInputComponent(panelFields, gc, "Password:", txtPassword);
        addComboBox(panelFields, gc, "Card Type:", txtCardType);
        addInputComponent(panelFields, gc, "Card Number:", txtCardNumber);
        addInputComponent(panelFields, gc, "Expiry Year:", txtExpiryYear);
        addInputComponent(panelFields, gc, "Expiry Month:", txtExpiryMonth);
        addInputComponent(panelFields, gc, "CSV:", txtCSV);

        // Load customer data into fields
        loadCustomerData(customer);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center buttons
        panelButtons.add(btnSave);
        panelButtons.add(btnCancel);
        add(panelFields, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);

    }

    private void addComboBox(JPanel panel, GridBagConstraints gc, String labelText, JComboBox<String> comboBox) {  // New method for JComboBox
        gc.gridx = 0;
        panel.add(new JLabel(labelText), gc);
        gc.gridx = 1;
        panel.add(comboBox, gc);
        gc.gridy++;
    }


    private void addInputComponent(JPanel panel, GridBagConstraints gc, String labelText, JComponent inputComponent) {
        gc.gridx = 0; // Label always in first column
        panel.add(new JLabel(labelText), gc);
        gc.gridx = 1; // Input component in second column
        panel.add(inputComponent, gc);
        gc.gridy++; // Move to the next row
    }

    private void loadCustomerData(Customer customer) {
        if (customer != null) {
            txtFirstName.setText(customer.getFirstName());
            txtLastName.setText(customer.getLastName());
            txtPhone.setText(customer.getPhone());
            txtEmail.setText(customer.getEmail());
            txtAddress.setText(customer.getAddress());
            txtCardNumber.setText(customer.getCardNumber());
            txtCSV.setText(customer.getCsv());
            txtExpiryYear.setText(customer.getExpiryYear());
            txtExpiryMonth.setText(customer.getExpiryMonth());
            txtCardType.setSelectedItem(customer.getCardType());
            txtUsername.setText(user.getUsername());
            txtPassword.setText(user.getPassword());
        }
    }

    public JTextField getTxtFirstName() {
        return txtFirstName;
    }
    public JTextField getTxtLastName() {
        return txtLastName;
    }

    public JTextField getTxtPhone() {
        return txtPhone;
    }

    public JTextField getTxtEmail() {
        return txtEmail;
    }

    public JTextField getTxtAddress() {
        return txtAddress;
    }

    public JTextField getTxtCardNumber() {
        return txtCardNumber;
    }

    public JTextField getTxtCSV() {
        return txtCSV;
    }

    public JTextField getTxtExpiryYear() {
        return txtExpiryYear;
    }

    public JTextField getTxtExpiryMonth() {
        return txtExpiryMonth;
    }

    public String getTxtCardType() {
        return txtCardType.getSelectedItem().toString();
    }

    public String getPassword(){
        return new String(txtPassword.getPassword());
    }

    public JButton getBtnSave() { return btnSave; }
    public JButton getBtnCancel() { return btnCancel; }
    public Customer getCustomer() { return this.customer; }
    public User getUser(){ return this.user; }
    public void setUser(User user){ this.user = user; }
    public void setCustomer(Customer customer){ this.customer = customer; }


}