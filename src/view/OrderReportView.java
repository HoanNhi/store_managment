// OrderReportView.java
package view;

import structure.Order;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class OrderReportView extends JFrame {
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JTextField txtOrderID = new JTextField(10);
    private JButton btnViewDetails = new JButton("View Details");
    private JButton btnDelete = new JButton("Delete Order");
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JButton btnFilterByDate;

    public OrderReportView() {
        this.setTitle("Order Report");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window
        // Date range filter components
        txtStartDate = new JTextField(10);
        txtEndDate = new JTextField(10);
        btnFilterByDate = new JButton("Filter by Date");

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Order ID");
        tableModel.addColumn("Customer ID");
        tableModel.addColumn("Order Date");
        tableModel.addColumn("Total Price");

        JPanel dateFilterPanel = new JPanel(new FlowLayout());
        dateFilterPanel.add(new JLabel("Start Date (yyyy-MM-dd):"));
        dateFilterPanel.add(txtStartDate);
        dateFilterPanel.add(new JLabel("End Date (yyyy-MM-dd):"));
        dateFilterPanel.add(txtEndDate);
        dateFilterPanel.add(btnFilterByDate);


        orderTable = new JTable(tableModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only single row selection
        JScrollPane scrollPane = new JScrollPane(orderTable);
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Panel for Order ID input and button
        JPanel inputPanel = new JPanel(new FlowLayout()); // Use FlowLayout for horizontal arrangement
        inputPanel.add(new JLabel("Order ID:"));
        inputPanel.add(txtOrderID);
        inputPanel.add(btnViewDetails);
        inputPanel.add(btnDelete);
        this.getContentPane().add(inputPanel, BorderLayout.SOUTH); // Add to the south (bottom)

        // Add date filter panel to the south along with other components
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(dateFilterPanel, BorderLayout.NORTH); // Date filter at top
        southPanel.add(inputPanel, BorderLayout.SOUTH);      // Order ID and other buttons at bottom

        this.getContentPane().add(southPanel, BorderLayout.SOUTH);

    }


    public void setOrders(List<Order> orders) { // Use a List<Order> to populate the table
        tableModel.setRowCount(0); // Clear existing rows

        for (Order order : orders) {
            Object[] rowData = {
                    order.getOrderID(),
                    order.getCustomerID(),
                    order.getDate(),
//                    order.getEmployeeID(),
                    order.getTotalCost()
            };
            tableModel.addRow(rowData);
        }
    }

    public JTable getOrderTable() {
        return orderTable;
    }

    public JButton getBtnViewDetails() {
        return btnViewDetails;
    }

    public JButton getBtnDelete() {
        return btnDelete;
    }

    public JTextField getTxtOrderID() {
        return txtOrderID;
    }

    public void clearOrderIDField(){
        txtOrderID.setText("");
    }

    public JButton getBtnFilterByDate() {
        return btnFilterByDate;
    }

    public Date getStartDate() {
        return parseDate(txtStartDate.getText());
    }

    public Date getEndDate() {
        return parseDate(txtEndDate.getText());
    }

    private Date parseDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Define your date format
            java.util.Date parsedUtilDate = dateFormat.parse(dateString); // Parse to java.util.Date first
            return new Date(parsedUtilDate.getTime()); // Convert to java.sql.Date

        } catch (ParseException e) {
            System.err.println("Invalid date format: " + dateString);
            // ... other error handling (e.g., show a dialog, log the error) ...
            return null;
        }
    }

}