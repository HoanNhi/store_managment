package controller;

import adapter.DataAdapter;
import structure.Product;
import view.ProductDetailView;

import java.util.List;

public class AllProductController {
    private ProductDetailView productDetailView;
    private DataAdapter dataAdapter;



    public AllProductController(ProductDetailView productDetailView, DataAdapter dataAdapter) {

        this.dataAdapter = dataAdapter;
        this.productDetailView = productDetailView;

        List<Product> products = this.dataAdapter.loadAllProducts(); // Load all product data

        if (products != null) {
            this.productDetailView.showProduct(products); // Pass product data to the view
            System.out.println(products.size());
        } else {
            // Handle the case where loading products fails. Maybe display error message
        }

    }
}
