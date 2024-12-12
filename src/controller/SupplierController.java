package controller;

import adapter.DataAdapter;
import main.Application;
import structure.*;
import view.SupplierView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SupplierController implements ActionListener {
    private SupplierView supplierView;
    private DataAdapter dataAdapter;

    public SupplierController(SupplierView view, DataAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
        this.supplierView = view;

        view.getBtnLoad().addActionListener(this);
        view.getBtnSave().addActionListener(this);
        view.getBtnDelete().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == supplierView.getBtnLoad()) {
            try {
                int supplierID = Integer.parseInt(supplierView.getTxtSupplierID().getText());
                loadSupplier(supplierID);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(supplierView, "Supplier ID invalid. Please try again");
            }
        } else if (e.getSource() == supplierView.getBtnSave()) {
            try {
                if (Application.getInstance().getCurrentUser().getRole() == User.UserRole.Manager) {
                    int supplierID = Integer.parseInt(supplierView.getTxtSupplierID().getText());
                    saveSupplier(supplierID);
                }
                else{
                    JOptionPane.showMessageDialog(supplierView, "User is not permitted to modify this supplier");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(supplierView, "Supplier ID invalid. Please try again");
            }
        }
        else if (e.getSource() == supplierView.getBtnDelete()) {
            try {
                if(Application.getInstance().getCurrentUser().getRole() == User.UserRole.Manager) {
                    int supplierID = Integer.parseInt(supplierView.getTxtSupplierID().getText());
                    deleteSupplier(supplierID);
                }
                else{
                    JOptionPane.showMessageDialog(supplierView, "User is not permitted to delete this supplier");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(supplierView, "Supplier ID invalid. Please try again");
            }
        }
    }

    private void loadSupplier(int supplierID) {
        try {
            Supplier supplier = dataAdapter.loadSupplier(supplierID);

            if (supplier == null) {
                JOptionPane.showMessageDialog(supplierView, "Supplier not found.");
                return; // Or clear fields, handle as needed
            }

            supplierView.getTxtSupplierName().setText(supplier.getName());
            supplierView.getTxtContactPerson().setText(supplier.getContactPerson());
            supplierView.getTxtPhone().setText(supplier.getPhone());

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(supplierView, "Invalid supplier ID format.");
        }
    }


    private void saveSupplier(int supplierID) {
        try {
            String name = supplierView.getTxtSupplierName().getText();
            String contactPerson = supplierView.getTxtContactPerson().getText();
            String phone = supplierView.getTxtPhone().getText();

            // Basic validation (you can add more robust validation)
            if (name.isEmpty() || contactPerson.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(supplierView, "Please fill in all fields.");
                return;
            }

            Supplier supplier = dataAdapter.loadSupplier(supplierID);

            if (supplier != null) { //Update supplier if it exists
                supplier.setName(name);
                supplier.setContactPerson(contactPerson);
                supplier.setPhone(phone);
            } else {  // Create new if it doesn't exist
                supplier = new Supplier();
                supplier.setSupplierID(supplierID);
                supplier.setName(name);
                supplier.setContactPerson(contactPerson);
                supplier.setPhone(phone);
            }

            if (dataAdapter.saveSupplier(supplier)) {
                supplierView.clearFields(); // Clear fields after successful save
                JOptionPane.showMessageDialog(supplierView, "Supplier saved successfully.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(supplierView, "Invalid input. Please check the ID and phone number.");
        }
    }

    private void deleteSupplier(int supplierID) {

        try {

            int confirmation = JOptionPane.showConfirmDialog(
                    supplierView,
                    "Are you sure you want to delete this supplier? " +
                            "All related products will also be deleted." ,
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                if (dataAdapter.deleteSupplier(supplierID)) {
                    supplierView.clearFields();
                    JOptionPane.showMessageDialog(supplierView, "Supplier deleted successfully!");
                    return; // Stop if product deletion fails
                } else {
                    JOptionPane.showMessageDialog(supplierView, "Failed to delete supplier.");
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(supplierView, "Invalid Supplier ID format.");

        }
    }

}