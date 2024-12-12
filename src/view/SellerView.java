//package view;
//
//import javax.swing.*;
//
//public class SellerView extends JFrame {
//    private JTextField txtProductID = new JTextField(10);
//    private JTextField txtProductName = new JTextField(30);
//    private JTextField txtProductCategory = new JTextField(10);
//    private JTextField txtProductSupplierID = new JTextField(10); // New field for SupplierID
//    private JTextField txtProductUnitPrice = new JTextField(10);  // Renamed for clarity
//    private JTextField txtProductDescription = new JTextField(30); // New field for Description
//    private JTextField txtProductQuantity = new JTextField(10);
//
//
//    private JButton btnLoad = new JButton("Load Product");
//    private JButton btnSave = new JButton("Save Product");
//    private JButton btnDelete = new JButton("Delete Product"); // Add a Delete button
//
//    public SellerView() {
//        this.setTitle("Manage Products");
//        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
//        this.setSize(800, 800);  // Increased size to accommodate new fields
//
//        // Button Panel
//        JPanel panelButton = new JPanel();
//        panelButton.add(btnLoad);
//        panelButton.add(btnSave);
//        panelButton.add(btnDelete);  //Add the Delete button to the button panel
//        this.getContentPane().add(panelButton);
//
//        // structure.Product ID Panel
//        JPanel panelProductID = new JPanel();
//        panelProductID.add(new JLabel("Product ID: "));
//        panelProductID.add(txtProductID);
//        txtProductID.setHorizontalAlignment(JTextField.LEFT);
//        this.getContentPane().add(panelProductID);
//
//        // structure.Product Name Panel
//        JPanel panelProductName = new JPanel();
//        panelProductName.add(new JLabel("Product Name: "));
//        panelProductName.add(txtProductName);
//        this.getContentPane().add(panelProductName);
//
//
//        // structure.Category ID panel
//        JPanel panelProductCategory = new JPanel();
//        panelProductCategory.add(new JLabel("Category: "));
//        panelProductCategory.add(txtProductCategory);
//        txtProductCategory.setHorizontalAlignment(JTextField.LEFT);
//        this.getContentPane().add(panelProductCategory);
//
//        // structure.Supplier ID panel
//        JPanel panelProductSupplierID = new JPanel();
//        panelProductSupplierID.add(new JLabel("Supplier ID:"));
//        panelProductSupplierID.add(txtProductSupplierID);
//        txtProductSupplierID.setHorizontalAlignment(JTextField.LEFT);
//        this.getContentPane().add(panelProductSupplierID);
//
//        // Unit Price and Description Panel
//        JPanel panelProductInfo = new JPanel();
//        panelProductInfo.add(new JLabel("Unit Price: ")); // Updated label
//        panelProductInfo.add(txtProductUnitPrice); // Updated variable name
//        txtProductUnitPrice.setHorizontalAlignment(JTextField.LEFT);
//
//        panelProductInfo.add(new JLabel("Description: "));
//        panelProductInfo.add(txtProductDescription);
//        this.getContentPane().add(panelProductInfo);
//
//        panelProductInfo.add(new JLabel("Quantity"));
//        panelProductInfo.add(txtProductQuantity);
//        this.getContentPane().add(panelProductInfo);
//    }
//
//    public JButton getBtnLoad() {
//        return btnLoad;
//    }
//
//    public JButton getBtnSave() {
//        return btnSave;
//    }
//
//    public JTextField getTxtProductID() {
//        return txtProductID;
//    }
//
//    public JTextField getTxtProductName() {
//        return txtProductName;
//    }
//
//    public JTextField getTxtProductPrice() {
//        return txtProductUnitPrice;
//    }
//
//    public JTextField getTxtProductQuantity() {
//        return txtProductQuantity;
//    }
//
//    public JTextField getTxtProductCategory() {
//        return txtProductCategory;
//    }
//
//    public JTextField getTxtProductSupplierID() {
//        return txtProductSupplierID;
//    }
//
//
//    public JTextField getTxtProductDescription() {
//        return txtProductDescription;
//    }
//
//    public JButton getBtnDelete() {
//        return btnDelete;
//    }
//
//}