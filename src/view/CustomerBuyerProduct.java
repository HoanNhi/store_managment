// ProductDetailView.java
package view;

import structure.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CustomerBuyerProduct extends JFrame {
    private JTable productTable; // Renamed for clarity
    private DefaultTableModel tableModel; // Renamed for clarity
    private final Dimension labelDimension = new Dimension(80, 20);  // Consistent with ManageUsersView
    private final Dimension inputBoxDimension = new Dimension(180, 20); // Consistent with ManageUsersView
    private final Dimension inputPanelDimension = new Dimension((int) (labelDimension.getWidth() + inputBoxDimension.getWidth()) + 20, 0);
    private JPanel inputPanel;
    private final Color mainColor = Color.white, inputColor = Color.black;
    private final Dimension buttonsDimension = new Dimension(105, 25);
    private String[] tableColumns;
    private String[][] userData;


    public CustomerBuyerProduct() {
        /**************************WINDOWS**************************/
        this.setTitle("Product Details");
        this.setSize(1028, 800); // Consistent size with ManageUsersView
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        /**************************WINDOWS**************************/
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

    public void updateTable() {
        int currentRowCount = tableModel.getRowCount();
        tableModel.setRowCount(0);
        tableModel.setRowCount(currentRowCount);
        tableModel.setDataVector(userData, tableColumns);
    }

}