// OrderReportController.java
package controller;


import adapter.DataAdapterMongo;
import main.Application;
import structure.Order;
import structure.User;
import view.OrderReportView;
import view.OrderDetailView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

public class OrderReportController implements ActionListener {


    private DataAdapterMongo dataAdapter;
    private OrderReportView orderReportView;
    private OrderDetailView orderDetailView;


    public OrderReportController(OrderReportView reportView, OrderDetailView detailView, DataAdapterMongo dataAdapter) {
        this.dataAdapter = dataAdapter;
        this.orderReportView = reportView;
        this.orderDetailView = detailView;
        orderReportView.getBtnViewDetails().addActionListener(this);
        orderReportView.getBtnDelete().addActionListener(this);
        orderReportView.getBtnFilterByDate().addActionListener(this);

        // Load and display order data
        List<Order> orders = dataAdapter.loadAllOrders();

        if (Application.getInstance().getCurrentUser().getRole() == User.UserRole.Customer){
            orders = orders.stream()
                    .filter(order -> order.getCustomerID() == Application.getInstance().getCurrentUser().getUserID())
                    .collect(Collectors.toList());
        }
        if (orders != null){
            System.out.println("Total order "+ orders.size());
            orderReportView.setOrders(orders);
        }
        orderReportView.getOrderTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) { // Check for left-click
                    JTable target = (JTable)e.getSource();
                    int selectedRow = target.getSelectedRow();
                    if (selectedRow != -1) { // Check if a row is selected
                        try {
                            int orderID = (int) target.getValueAt(selectedRow, 0); // Get order ID from first column
                            orderReportView.getTxtOrderID().setText(String.valueOf(orderID)); // Set in text field
                        } catch (ClassCastException ex) { //Handle potential ClassCastException
                            JOptionPane.showMessageDialog(orderReportView, "Invalid data format in the selected cell.");
                        }

                    }
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == orderReportView.getBtnViewDetails()) {
            try {
                int orderId = Integer.parseInt(orderReportView.getTxtOrderID().getText());
                showOrderDetails(orderId);
                orderReportView.clearOrderIDField();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(orderReportView, "Invalid Order ID format. Please enter a number.");
            }
        }
        if (e.getSource() == orderReportView.getBtnDelete()) {
            try {
                int orderID = Integer.parseInt(orderReportView.getTxtOrderID().getText());
                deleteOrder(orderID);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(orderReportView, "Invalid Order ID format. Please enter a number.");
            }
        }

        if (e.getSource() == orderReportView.getBtnFilterByDate()) {
            Date startDate = orderReportView.getStartDate();
            Date endDate = orderReportView.getEndDate();

            if (startDate != null && endDate != null) { // Check if dates are valid
                List<Order> filteredOrders = dataAdapter.loadOrdersByDateRange(startDate, endDate); // Example method in DataAdapter
                orderReportView.setOrders(filteredOrders);
            } else {
                JOptionPane.showMessageDialog(orderReportView, "Invalid Date input");
            }
        }
    }

    private void showOrderDetails(int orderId){
        Order order = dataAdapter.loadOrder(orderId); //Load order based on selected order id

        if (order != null) {
            orderDetailView.setOrder(order); //Set the order in the orderDetailView
            orderDetailView.setVisible(true);

        } else {
            JOptionPane.showMessageDialog(orderReportView, "Error loading order details.");

        }
    }

    private void deleteOrder(int orderID) {
        try {

            int confirmation = JOptionPane.showConfirmDialog(
                    orderReportView,
                    "Are you sure you want to delete this order and all associated items?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {


                if (dataAdapter.deleteOrder(orderID)) {
                    // Refresh the order table after deletion
                    List<Order> updatedOrders = dataAdapter.loadAllOrders();
                    orderReportView.setOrders(updatedOrders);


                    orderReportView.clearOrderIDField(); //Clear input field after deleting
                    JOptionPane.showMessageDialog(orderReportView, "Order deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(orderReportView, "Failed to delete order.");
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(orderReportView, "Invalid Order ID format.");

        }

    }

}