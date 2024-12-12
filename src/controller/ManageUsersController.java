package controller;

import adapter.*;
import main.Application;
import structure.Customer;
import structure.User;
import view.ManageUsersView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManageUsersController implements ActionListener {

    private ManageUsersView manageUsersView;
    private DataAdapter dataAdapter;
    private DataAdapterMongo dataAdapterMongo;

    public ManageUsersController(ManageUsersView manageUsersView, DataAdapter dataAdapter,
                                                                DataAdapterMongo dataAdapterMongo) {
        this.manageUsersView = manageUsersView;
        this.dataAdapter = dataAdapter;
        this.dataAdapterMongo = dataAdapterMongo;

        manageUsersView.setUserDataTable(this.dataAdapter.loadAllUsersData());
        manageUsersView.updateTable();

        manageUsersView.getCreateUserButton().addActionListener(this);
        manageUsersView.getDeleteUserButton().addActionListener(this);
        manageUsersView.getUpdateUserButton().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(manageUsersView.getCreateUserButton())) {
            createUser();
            manageUsersView.clearInputBoxes();
        }
        else if (e.getSource().equals(manageUsersView.getDeleteUserButton())) {
            deleteUser();
            manageUsersView.clearInputBoxes();
        }
        else if (e.getSource().equals(manageUsersView.getUpdateUserButton())) {
            updateUser();
            manageUsersView.clearInputBoxes();
        }
    }

    public void createUser() {
        if (manageUsersView.areBoxesEmpty()){
            JOptionPane.showMessageDialog(manageUsersView, "One of the field is empty! Please try again!",
                                                                                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        User user = new User();
        user.setUserID(Integer.parseInt(manageUsersView.getUserIDTextField().getText()));
        user.setUsername(manageUsersView.getUsernameTextField().getText());
        user.setEmail(manageUsersView.getEmailTextField().getText());
        user.setPassword(manageUsersView.getPasswordTextField().getText());
        user.setFirstName(manageUsersView.getFirstNameTextField().getText());
        user.setLastName(manageUsersView.getLastNameTextField().getText());
        user.setAddress(manageUsersView.getAddressTextField().getText());
        user.setPhone(manageUsersView.getPhoneTextField().getText());
        user.setRole(User.UserRole.valueOf(manageUsersView.getUserRole()));
        dataAdapter.addUser(user);

        if (user.getRole() == User.UserRole.Customer) {
            Customer customer = new Customer();
            customer.setUserID(user.getUserID());
            customer.setFirstName(user.getFirstName());
            customer.setLastName(user.getLastName());
            customer.setAddress(user.getAddress());
            customer.setEmail(user.getEmail());
            customer.setPhone(user.getPhone());
            this.dataAdapterMongo.createCustomer(customer);
        }

        manageUsersView.setUserDataTable(this.dataAdapter.loadAllUsersData());
        manageUsersView.updateTable();

        JOptionPane.showMessageDialog(manageUsersView, "User created successfully!", "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void updateUser(){
        if (manageUsersView.areBoxesEmpty()){
            JOptionPane.showMessageDialog(manageUsersView, "One of the field is empty! Please try again!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (Integer.parseInt(manageUsersView.getUserIDTextField().getText()) == Application.getInstance().getCurrentUser().getUserID()){
            JOptionPane.showMessageDialog(manageUsersView, "Cannot modify the current user",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        User user = new User();
        user.setUserID(Integer.parseInt(manageUsersView.getUserIDTextField().getText()));
        user.setUsername(manageUsersView.getUsernameTextField().getText());
        user.setEmail(manageUsersView.getEmailTextField().getText());
        user.setPassword(manageUsersView.getPasswordTextField().getText());
        user.setFirstName(manageUsersView.getFirstNameTextField().getText());
        user.setLastName(manageUsersView.getLastNameTextField().getText());
        user.setAddress(manageUsersView.getAddressTextField().getText());
        user.setPhone(manageUsersView.getPhoneTextField().getText());
        user.setRole(User.UserRole.valueOf(manageUsersView.getUserRole()));
        dataAdapter.updateUser(user);

        manageUsersView.setUserDataTable(this.dataAdapter.loadAllUsersData());
        manageUsersView.updateTable();

        JOptionPane.showMessageDialog(manageUsersView, "User updated successfully!", "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void deleteUser() {
        if (manageUsersView.areBoxesEmpty()){
            JOptionPane.showMessageDialog(manageUsersView, "One of the field is empty! Please try again!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int userID = Integer.parseInt(manageUsersView.getUserIDTextField().getText());
        if (userID == Application.getInstance().getCurrentUser().getUserID()){
                JOptionPane.showMessageDialog(manageUsersView, "Cannot delete the current user",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }
        int confirmation = JOptionPane.showConfirmDialog(
                manageUsersView,
                "Are you sure you want to delete this user?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );
        if (confirmation == JOptionPane.YES_OPTION) {
            if(dataAdapter.deleteUser(userID)){
                manageUsersView.clearInputBoxes();
                JOptionPane.showMessageDialog(manageUsersView, "User deleted successfully.");

                manageUsersView.setUserDataTable(this.dataAdapter.loadAllUsersData());
                manageUsersView.updateTable();
            }
            else{
                JOptionPane.showMessageDialog(manageUsersView, "Failed to delete user.");
            }
        }
    }
}
