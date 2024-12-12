package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;


public class ManageUsersView extends JFrame{
    private final JPanel bottomPanel, inputsPanel, tablePanel;
    private final JLabel userIDLabel, usernameLabel, firstNameLabel, lastNameLabel, phoneLabel, addressLabel,
                                                        passwordLabel, emailLabel, userAccessLevelLabel, instructionLabel;
    private final JTextField userIDTextField, usernameTextField, firstNameTextField, lastNameTextField, phoneTextField,
                                                                    addressTextField, passwordTextField, emailTextField;
    private final JComboBox<String> userAccessLevelComboBox;
    private final JButton createUserButton, updateUserButton, deleteUserButton;
    private final Dimension labelDimension = new Dimension(80, 20), inputBoxDimension = new Dimension(180, 20),
            inputPanelDimension = new Dimension((int)(labelDimension.getWidth() + inputBoxDimension.getWidth()) + 20, 0),
            tableDimension = new Dimension(700, 700), buttonsDimension = new Dimension(105, 25);
    private final Color mainColor = Color.white, inputColor = Color.black;
    private final DefaultTableModel tableModel;
    private final JTable userDataTable;
    private final JScrollPane scrollPane;
    private Object[][] userData;
    private final String[] tableColumns;


    public ManageUsersView(){

        /****************************** Frame ******************************/

        this.setTitle("Manage users");
        this.setSize(1028, 800);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        inputsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        inputsPanel.setPreferredSize(inputPanelDimension);
        inputsPanel.setBackground(mainColor);
        this.add(inputsPanel, BorderLayout.WEST);

        tablePanel = new JPanel(new GridBagLayout());
        tablePanel.setBackground(mainColor);
        this.add(tablePanel, BorderLayout.CENTER);

        /****************************** Frame ******************************/
        /****************************** Input ******************************/

        userIDLabel = new JLabel("User ID");
        userIDTextField = new JTextField();
        setTextFieldDesign(userIDLabel, userIDTextField);

        usernameLabel = new JLabel("Username");
        usernameTextField = new JTextField();
        setTextFieldDesign(usernameLabel, usernameTextField);

        firstNameLabel = new JLabel("First name");
        firstNameTextField = new JTextField();
        setTextFieldDesign(firstNameLabel, firstNameTextField);

        lastNameLabel = new JLabel("Last name");
        lastNameTextField = new JTextField();
        setTextFieldDesign(lastNameLabel, lastNameTextField);

        phoneLabel = new JLabel("Phone");
        phoneTextField = new JTextField();
        setTextFieldDesign(phoneLabel, phoneTextField);

        addressLabel = new JLabel("Address");
        addressTextField = new JTextField();
        setTextFieldDesign(addressLabel, addressTextField);

        passwordLabel = new JLabel("Password");
        passwordTextField = new JTextField();
        setTextFieldDesign(passwordLabel, passwordTextField);

        emailLabel = new JLabel("E-mail");
        emailTextField = new JTextField();
        setTextFieldDesign(emailLabel, emailTextField);

        userAccessLevelLabel = new JLabel("Access");
        userAccessLevelLabel.setPreferredSize(labelDimension);
        userAccessLevelLabel.setFont(new Font("Calibri", Font.BOLD, 14));
        inputsPanel.add(userAccessLevelLabel);

        userAccessLevelComboBox = new JComboBox<>(new String[]{null, "Manager", "Seller", "Customer"});
        userAccessLevelComboBox.setPreferredSize(inputBoxDimension);
        userAccessLevelComboBox.setFocusable(false);
        inputsPanel.add(userAccessLevelComboBox);

        /****************************** Input ******************************/
        /***************************** Buttons *****************************/

        createUserButton = new JButton("Create user");
        setButtonDesign(createUserButton);
        inputsPanel.add(createUserButton);

        updateUserButton = new JButton("Update User");
        setButtonDesign(updateUserButton);
        inputsPanel.add(updateUserButton);

        instructionLabel = new JLabel("Select from table to delete");
        instructionLabel.setFont(new Font("Calibri", Font.BOLD, 10));
        inputsPanel.add(instructionLabel);

        deleteUserButton = new JButton("Delete User");
        setButtonDesign(deleteUserButton);
        inputsPanel.add(deleteUserButton);

        /***************************** Buttons *****************************/
        /****************************** Table ******************************/


        tableColumns = new String[]{"User ID", "Username", "Password",
                "First name", "Last name", "E-mail", "Phone", "Address", "Access Level"};
        tableModel = new DefaultTableModel(userData, tableColumns);

        userDataTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userDataTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2 && event.getButton() == MouseEvent.BUTTON1) {
                    int selectedRow = userDataTable.getSelectedRow();
                    if (selectedRow != -1) { // Check if a row is actually selected
                        userIDTextField.setText((String) userDataTable.getValueAt(selectedRow, 0));
                        usernameTextField.setText((String) userDataTable.getValueAt(selectedRow, 1));
                        passwordTextField.setText((String) userDataTable.getValueAt(selectedRow, 2));
                        firstNameTextField.setText((String) userDataTable.getValueAt(selectedRow, 3)); // Added
                        lastNameTextField.setText((String) userDataTable.getValueAt(selectedRow, 4));  // Added
                        emailTextField.setText((String) userDataTable.getValueAt(selectedRow, 5));
                        phoneTextField.setText((String) userDataTable.getValueAt(selectedRow, 6));   // Added
                        addressTextField.setText((String) userDataTable.getValueAt(selectedRow, 7));  // Added
                        userAccessLevelComboBox.setSelectedItem(userDataTable.getValueAt(selectedRow, 8));

                    }
                }
            }
        });

        scrollPane = new JScrollPane(userDataTable);
        scrollPane.setPreferredSize(tableDimension);
        tablePanel.add(scrollPane, new GridBagConstraints());

        /****************************** Table ******************************/
        /****************************** Frame ******************************/

        bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 5));
        bottomPanel.setPreferredSize(new Dimension(0, 50));
        bottomPanel.setBackground(mainColor);
        this.add(bottomPanel, BorderLayout.SOUTH);

        this.setVisible(true);

        /****************************** Frame ******************************/
    }

    private void setTextFieldDesign(JLabel label, JTextField textField){
        label.setPreferredSize(labelDimension);
        label.setFont(new Font("Calibri", Font.BOLD, 14));
        inputsPanel.add(label);

        textField.setPreferredSize(inputBoxDimension);
        textField.setForeground(inputColor);
        textField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, inputColor));
        inputsPanel.add(textField);
    }

    private void setButtonDesign(JButton button){
        button.setPreferredSize(buttonsDimension);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createLineBorder(inputColor));
        button.setBackground(mainColor);
        button.setForeground(inputColor);
    }

    public void updateTable() {
        int currentRowCount = tableModel.getRowCount();
        tableModel.setRowCount(0);
        tableModel.setRowCount(currentRowCount);
        tableModel.setDataVector(userData, tableColumns);
    }

    public JTextField getUsernameTextField(){
        return usernameTextField;
    }

    public JTextField getUserIDTextField(){
        return userIDTextField;
    }

    public JTextField getEmailTextField() {
        return emailTextField;
    }

    public JTextField getFirstNameTextField() {
        return firstNameTextField;
    }

    public JTextField getLastNameTextField() {
        return lastNameTextField;
    }

    public JTextField getPhoneTextField() {
        return phoneTextField;
    }

    public JTextField getAddressTextField() {
        return addressTextField;
    }

    public JTextField getPasswordTextField(){
        return passwordTextField;
    }

    public String getUserRole(){
        return userAccessLevelComboBox.getSelectedItem().toString();
    }

    public JButton getCreateUserButton() {
        return createUserButton;
    }

    public JButton getUpdateUserButton() {
        return updateUserButton;
    }

    public JButton getDeleteUserButton() {
        return deleteUserButton;
    }

    public void setUserDataTable(String[][] userData) {
        this.userData = userData;
    }

    public boolean areBoxesEmpty() {
        return usernameTextField.getText().isBlank() ||
                passwordTextField.getText().isBlank() ||
                emailTextField.getText().isBlank() ||
                firstNameTextField.getText().isBlank() ||
                lastNameTextField.getText().isBlank() ||
                phoneTextField.getText().isBlank() ||
                addressTextField.getText().isBlank() ||
                userAccessLevelComboBox.getSelectedItem() == null;
    }

    public void clearInputBoxes() {
        usernameTextField.setText("");
        passwordTextField.setText("");
        emailTextField.setText("");
        firstNameTextField.setText("");
        lastNameTextField.setText("");
        phoneTextField.setText("");
        addressTextField.setText("");
        userAccessLevelComboBox.setSelectedIndex(0);
        usernameTextField.requestFocus(); // Optional: set focus back to the first field
    }
}