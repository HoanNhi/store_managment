//OrderDetailView.java
package view;

import adapter.*;
import structure.Order;
import structure.OrderItem;
import structure.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderDetailView extends JFrame {
    private JLabel lblOrderID = new JLabel("Order ID:");
    private JLabel lblCustomerID = new JLabel("Customer ID:");
    private JLabel lblOrderDate = new JLabel("Order Date:");
    private JLabel lblEmployeeID = new JLabel("Employee ID:");
    private JLabel lblTotalCost = new JLabel("Total Cost:");
    DataAdapterMongo adapterMongo;
    DataAdapter adapterSQL;


    private JTable orderItemsTable;
    private DefaultTableModel itemsTableModel;

    public OrderDetailView(DataAdapterMongo adapter, DataAdapter adapterSQL) {
        this.setTitle("Order Details");
        this.setSize(600, 400);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.adapterMongo = adapter;
        this.adapterSQL = adapterSQL;


        JPanel panelOrderDetails = new JPanel(new GridLayout(5,1));
        panelOrderDetails.add(lblOrderID);
        panelOrderDetails.add(lblCustomerID);
        panelOrderDetails.add(lblOrderDate);
//        panelOrderDetails.add(lblEmployeeID);
        panelOrderDetails.add(lblTotalCost);


        add(panelOrderDetails, BorderLayout.NORTH);



        //Order Items Table
        itemsTableModel = new DefaultTableModel();
        itemsTableModel.addColumn("Product ID");
        itemsTableModel.addColumn("Product Name");
        itemsTableModel.addColumn("Quantity");
        itemsTableModel.addColumn("Cost");

        orderItemsTable = new JTable(itemsTableModel);
        JScrollPane scrollPane = new JScrollPane(orderItemsTable);

        add(scrollPane, BorderLayout.CENTER);

    }


    public void setOrder(Order order){
        lblOrderID.setText("Order ID: " + order.getOrderID());
        lblCustomerID.setText("Customer ID: " + order.getCustomerID());
        lblOrderDate.setText("Order Date: " + order.getDate());
//        lblEmployeeID.setText("Employee ID: " + order.getEmployeeID());
        lblTotalCost.setText("Total Cost: " + order.getTotalCost());


        itemsTableModel.setRowCount(0); // Clear existing table data

        List<OrderItem> orderItems = order.getLines();

        if (orderItems != null) {

            for(OrderItem item : orderItems) {
                Product product = this.adapterSQL.loadProduct(item.getProductID());
                Object[] row = new Object[4];
                row[0] = item.getProductID();
                row[1] = product.getName();
                row[2] = item.getQuantity();
                row[3] = item.getCost();
                itemsTableModel.addRow(row);

            }
        }

    }

}