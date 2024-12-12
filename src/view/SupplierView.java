// SupplierView.java
package view;

import javax.swing.*;
import java.awt.*;

public class SupplierView extends JFrame {
    private JTextField txtSupplierID = new JTextField(10);
    private JTextField txtSupplierName = new JTextField(30);
    private JTextField txtContactPerson = new JTextField(30);
    private JTextField txtPhone = new JTextField(20);

    private JButton btnLoad = new JButton("Load Supplier");
    private JButton btnSave = new JButton("Save Supplier");
    private JButton btnDelete = new JButton("Delete Supplier");

    public SupplierView() {
        this.setTitle("Manage Suppliers");
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        this.setSize(500, 200);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelButton = new JPanel();
        panelButton.add(btnLoad);
        panelButton.add(btnSave);
        panelButton.add(btnDelete);
        this.getContentPane().add(panelButton);

        JPanel panelSupplierID = new JPanel();
        panelSupplierID.add(new JLabel("Supplier ID:"));
        panelSupplierID.add(txtSupplierID);
        this.getContentPane().add(panelSupplierID);


        JPanel panelSupplierName = new JPanel();
        panelSupplierName.add(new JLabel("Supplier Name:"));
        panelSupplierName.add(txtSupplierName);
        this.getContentPane().add(panelSupplierName);

        JPanel panelContactPerson = new JPanel();
        panelContactPerson.add(new JLabel("Contact Person:"));
        panelContactPerson.add(txtContactPerson);
        this.getContentPane().add(panelContactPerson);


        JPanel panelPhone = new JPanel();
        panelPhone.add(new JLabel("Phone:"));
        panelPhone.add(txtPhone);
        this.getContentPane().add(panelPhone);



    }

    // Getters for all the components
    public JButton getBtnLoad() {
        return btnLoad;
    }

    public JButton getBtnSave() {
        return btnSave;
    }

    public JButton getBtnDelete() {
        return btnDelete;
    }

    public JTextField getTxtSupplierID() {
        return txtSupplierID;
    }

    public JTextField getTxtSupplierName() {
        return txtSupplierName;
    }

    public JTextField getTxtContactPerson() {
        return txtContactPerson;
    }

    public JTextField getTxtPhone() {
        return txtPhone;
    }


    public void clearFields() {  // Method to clear all text fields
        txtSupplierID.setText("");
        txtSupplierName.setText("");
        txtContactPerson.setText("");
        txtPhone.setText("");
    }

}