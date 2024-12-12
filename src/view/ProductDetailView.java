// ProductDetailView.java
package view;

import structure.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.List;

public class ProductDetailView extends JFrame {
    private JTable productTable; // Renamed for clarity
    private DefaultTableModel tableModel; // Renamed for clarity
    private final Dimension labelDimension = new Dimension(80, 20);  // Consistent with ManageUsersView
    private final Dimension inputBoxDimension = new Dimension(180, 20); // Consistent with ManageUsersView
    private final Dimension inputPanelDimension = new Dimension((int) (labelDimension.getWidth() + inputBoxDimension.getWidth()) + 20, 0);
    private JPanel inputPanel;
    private final Color mainColor = Color.white, inputColor = Color.black;
    private JTextField txtProductID;
    private JTextField txtProductName;
    private JTextField txtCategory;
    private JTextField txtSupplierID;
    private JTextField txtUnitPrice;
    private JTextField txtDescription;
    private JTextField txtQuantity;
    private final JLabel instructionLabel;
    private final JButton btnCreate, btnUpdate, btnDelete;
    private final Dimension buttonsDimension = new Dimension(105, 25);
    private String[] tableColumns;
    private String[][] userData;


    public ProductDetailView() {
        /**************************WINDOWS**************************/
        this.setTitle("Product Details");
        this.setSize(1028, 800); // Consistent size with ManageUsersView
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        /**************************WINDOWS**************************/
        /**************************PANEL**************************/
        inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20)); // Use FlowLayout, similar to ManageUsersView
        inputPanel.setPreferredSize(inputPanelDimension);
        inputPanel.setBackground(mainColor);
        this.add(inputPanel, BorderLayout.WEST);
        /**************************PANEL**************************/
        /**************************TABLE**************************/
        // Product Table
        tableColumns = new String[]{"Product ID", "Product Name", "Category", "Supplier ID", "Unit Price",
                                                                                    "Description", "Quantity"};
        tableModel = new DefaultTableModel(userData, tableColumns);

        productTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JScrollPane scrollPane = new JScrollPane(productTable);

        this.add(scrollPane, BorderLayout.CENTER);
        /**************************TABLE**************************/
        /**************************INPUT FIELD**************************/
        JLabel productIDLabel = new JLabel("Product ID");
        txtProductID = new JTextField();
        setTextFieldDesign(productIDLabel, txtProductID);

        JLabel productNameLabel = new JLabel("Product Name");
        txtProductName = new JTextField();
        setTextFieldDesign(productNameLabel, txtProductName);


        JLabel categoryLabel = new JLabel("Category");
        txtCategory = new JTextField();
        setTextFieldDesign(categoryLabel, txtCategory);


        JLabel supplierIDLabel = new JLabel("Supplier ID");
        txtSupplierID = new JTextField();
        setTextFieldDesign(supplierIDLabel, txtSupplierID);

        JLabel unitPriceLabel = new JLabel("Unit Price");
        txtUnitPrice = new JTextField();
        setTextFieldDesign(unitPriceLabel, txtUnitPrice);

        JLabel descriptionLabel = new JLabel("Description");
        txtDescription = new JTextField();
        setTextFieldDesign(descriptionLabel, txtDescription);

        JLabel quantityLabel = new JLabel("Quantity");
        txtQuantity = new JTextField();
        setTextFieldDesign(quantityLabel, txtQuantity);

        btnCreate = new JButton("Create product");
        setButtonDesign(btnCreate);
        inputPanel.add(btnCreate);

        btnUpdate = new JButton("Update Product");
        setButtonDesign(btnUpdate);
        inputPanel.add(btnUpdate);

        instructionLabel = new JLabel("Select from table to delete");
        instructionLabel.setFont(new Font("Calibri", Font.BOLD, 10));
        inputPanel.add(instructionLabel);

        btnDelete = new JButton("Delete Product");
        setButtonDesign(btnDelete);
        inputPanel.add(btnDelete);
        /**************************INPUT FIELD**************************/
        /**************************TABLE LISTENER**************************/

        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2 && event.getButton() == MouseEvent.BUTTON1) {
                    int selectedRow = productTable.getSelectedRow();
                    if (selectedRow != -1) { // Check if a row is actually selected
                        txtProductID.setText(String.valueOf(productTable.getValueAt(selectedRow, 0)));
                        txtProductName.setText(String.valueOf(productTable.getValueAt(selectedRow, 1)));
                        txtCategory.setText(String.valueOf(productTable.getValueAt(selectedRow, 2)));
                        txtSupplierID.setText(String.valueOf(productTable.getValueAt(selectedRow, 3)));
                        txtUnitPrice.setText(String.valueOf(productTable.getValueAt(selectedRow, 4)));
                        txtDescription.setText(String.valueOf(productTable.getValueAt(selectedRow, 5)));
                        txtQuantity.setText(String.valueOf(productTable.getValueAt(selectedRow, 6)));
                    }
                }
            }
        });
        /**************************TABLE LISTENER**************************/
    }

    private void setTextFieldDesign(JLabel label, JTextField textField) {
        label.setPreferredSize(labelDimension);
        label.setFont(new Font("Calibri", Font.BOLD, 14));
        inputPanel.add(label);

        textField.setPreferredSize(inputBoxDimension);
        textField.setForeground(inputColor);
        textField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, inputColor));
        inputPanel.add(textField);
    }

    private void setButtonDesign(JButton button){
        button.setPreferredSize(buttonsDimension);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createLineBorder(inputColor));
        button.setBackground(mainColor);
        button.setForeground(inputColor);
    }


    public void showProduct(List<Product> products) {
        tableModel.setRowCount(0);
        for (Product product : products) {
            Object[] row = {
                    product.getProductID(),
                    product.getName(),
                    product.getCategory(),
                    product.getSupplierID(),
                    product.getPrice(),
                    product.getDescription(),
                    product.getQuantity()
            };
            tableModel.addRow(row);
        }
    }

    public void setProductTable(String[][] userData) {
        this.userData = userData;
    }

    public boolean areProductInputBoxesEmpty() {
        return txtProductID.getText().isBlank() ||
                txtProductName.getText().isBlank() ||
                txtCategory.getText().isBlank() ||
                txtSupplierID.getText().isBlank() ||
                txtUnitPrice.getText().isBlank() ||
                txtDescription.getText().isBlank() ||
                txtQuantity.getText().isBlank();
    }

    public void clearProductInputBoxes() {
        txtProductID.setText("");
        txtProductName.setText("");
        txtCategory.setText("");
        txtSupplierID.setText("");
        txtUnitPrice.setText("");
        txtDescription.setText("");
        txtQuantity.setText("");
        txtProductID.requestFocus(); // Optional: Set focus to the first field
    }

    public void updateTable() {
        int currentRowCount = tableModel.getRowCount();
        tableModel.setRowCount(0);
        tableModel.setRowCount(currentRowCount);
        tableModel.setDataVector(userData, tableColumns);
    }

    public JButton getBtnCreate() {
        return btnCreate;
    }

    public JButton getBtnUpdate() {
        return btnUpdate;
    }

    public JButton getBtnDelete() {
        return btnDelete;
    }
    public JTextField getTxtProductID() {
        return txtProductID;
    }

    public JTextField getTxtProductName() {
        return txtProductName;
    }

    public JTextField getTxtProductCategory() {
        return txtCategory;
    }

    public JTextField getTxtProductSupplierID() {
        return txtSupplierID;
    }

    public JTextField getTxtProductPrice() {
        return txtUnitPrice;
    }

    public JTextField getTxtProductDescription() {
        return txtDescription;
    }

    public JTextField getTxtProductQuantity() {
        return txtQuantity;
    }
}