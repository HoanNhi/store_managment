// CustomerEditController.java (Controller)
package controller;

import adapter.*; // Or DataAdapterInterface
import main.Application;
import structure.Customer;
import structure.User;
import view.CustomerEditView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomerEditController {

    private final CustomerEditView customerEditView;
    private final DataAdapterInterface dataAdapterMongo;
    private final DataAdapterInterface dataAdapterSQL;

    public CustomerEditController(CustomerEditView view, DataAdapterInterface adapterMongo, DataAdapterInterface adapterSQL) {
        this.customerEditView = view;
        this.dataAdapterMongo = adapterMongo;
        this.dataAdapterSQL = adapterSQL;

        //Save button action listener

        customerEditView.getBtnSave().addActionListener(e -> saveCustomerChanges());

        customerEditView.getBtnCancel().addActionListener(e -> customerEditView.dispose());

    }

    private void saveCustomerChanges() {
        Customer updatedCustomer = getUpdatedCustomer();
        User userCustomer = getUpdatedUser();
        //Update card information
        updatedCustomer.setCard(customerEditView.getTxtCardNumber().getText(),
                customerEditView.getTxtCSV().getText(),
                customerEditView.getTxtExpiryYear().getText(),
                customerEditView.getTxtExpiryMonth().getText(),
                customerEditView.getTxtCardType());

        if (dataAdapterMongo.updateCustomer(updatedCustomer) && dataAdapterSQL.updateUser(userCustomer)) {
            JOptionPane.showMessageDialog(customerEditView, "Customer information updated successfully!");
        } else {
            JOptionPane.showMessageDialog(customerEditView, "Error updating customer information.");
        }
    }

    private User getUpdatedUser() {
        User user = customerEditView.getUser();
        user.setFirstName(customerEditView.getTxtFirstName().getText());
        user.setLastName(customerEditView.getTxtLastName().getText());
        user.setAddress(customerEditView.getTxtAddress().getText());
        user.setEmail(customerEditView.getTxtEmail().getText());
        user.setPhone(customerEditView.getTxtPhone().getText());
        user.setPassword(customerEditView.getPassword());
        return user;
    }

    private Customer getUpdatedCustomer() {
        Customer updatedCustomer = customerEditView.getCustomer();

        // Update customer data from the text fields (add validation if needed)
        updatedCustomer.setFirstName(customerEditView.getTxtFirstName().getText());
        updatedCustomer.setLastName(customerEditView.getTxtLastName().getText());
        updatedCustomer.setAddress(customerEditView.getTxtAddress().getText());
        updatedCustomer.setEmail(customerEditView.getTxtEmail().getText());
        updatedCustomer.setPhone(customerEditView.getTxtPhone().getText());
        return updatedCustomer;
    }

}