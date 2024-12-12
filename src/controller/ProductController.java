package controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import adapter.DataAdapter;

import structure.Product;
import view.ProductDetailView;

public class ProductController implements ActionListener {
    private ProductDetailView productView;
    private DataAdapter dataAdapter; // to save and load product information

    public ProductController(ProductDetailView productView, DataAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
        this.productView = productView;

        productView.getBtnCreate().addActionListener(this);
        productView.getBtnUpdate().addActionListener(this);
        productView.getBtnDelete().addActionListener(this); // Add ActionListener to the delete button
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == productView.getBtnCreate()) {
            saveProduct();
        } else if (e.getSource() == productView.getBtnUpdate()) {
            updateProduct();
        } else if (e.getSource() == productView.getBtnDelete()) { // Handle Delete button click
            deleteProduct();
        }

    }

    private void deleteProduct() {
        try {
            int productID = Integer.parseInt(productView.getTxtProductID().getText());

            // Confirmation dialog before deletion (recommended)
            int confirmation = JOptionPane.showConfirmDialog(
                    productView,
                    "Are you sure you want to delete this product?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                if (dataAdapter.deleteProduct(productID)) {
                    productView.clearProductInputBoxes();
                    JOptionPane.showMessageDialog(productView, "Product deleted successfully.");
                    productView.clearProductInputBoxes();
                    String[][] productData = dataAdapter.loadAllProductsData();
                    productView.setProductTable(productData);
                    productView.updateTable();
                } else {
                    JOptionPane.showMessageDialog(productView, "Failed to delete product.");
                }
            }


        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid product ID format.");
        }
    }

    private void updateProduct() {
        if(productView.areProductInputBoxesEmpty()){
            JOptionPane.showMessageDialog(null, "One of the field is not filled");
            return;
        };

        Product product = new Product();
        product.setProductID(Integer.parseInt(productView.getTxtProductID().getText()));
        product.setSupplierID(Integer.parseInt(productView.getTxtProductSupplierID().getText()));
        product.setName(productView.getTxtProductName().getText());
        product.setCategory(productView.getTxtProductCategory().getText());
        product.setPrice(Double.parseDouble(productView.getTxtProductPrice().getText()));
        product.setQuantity(Integer.parseInt(productView.getTxtProductQuantity().getText()));
        product.setDescription(productView.getTxtProductDescription().getText());

        boolean result = dataAdapter.saveProduct(product);

        if (!result){
            JOptionPane.showMessageDialog(null, "Something went wrong! Please try again!");
            return;
        }

        productView.clearProductInputBoxes();
        String[][] productData = dataAdapter.loadAllProductsData();
        productView.setProductTable(productData);
        productView.updateTable();
        JOptionPane.showMessageDialog(null, "Product saved successfully.");
    }

    private void saveProduct() {
        try {
            Product product = new Product();
            product.setProductID(Integer.parseInt(productView.getTxtProductID().getText()));
            product.setName(productView.getTxtProductName().getText());
            product.setCategory(productView.getTxtProductCategory().getText());
            product.setSupplierID(Integer.parseInt(productView.getTxtProductSupplierID().getText()));
            product.setPrice(Double.parseDouble(productView.getTxtProductPrice().getText()));
            product.setDescription(productView.getTxtProductDescription().getText());
            product.setQuantity(Integer.parseInt(productView.getTxtProductQuantity().getText()));
            if(productView.areProductInputBoxesEmpty()){
                JOptionPane.showMessageDialog(null, "One of the field is not filled");
                return;
            }
            if (dataAdapter.saveProduct(product)) {
                JOptionPane.showMessageDialog(null, "Product saved successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Error saving product.");
            }
            productView.clearProductInputBoxes();
            String[][] productData = dataAdapter.loadAllProductsData();
            productView.setProductTable(productData);
            productView.updateTable(); // Refresh the table after saving

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input format. Please check numeric fields.");
        } catch (Exception e) {  // Catch more general exceptions
            JOptionPane.showMessageDialog(null, "Error saving product: " + e.getMessage());
            e.printStackTrace(); // Or log the error appropriately
        }
    }

}