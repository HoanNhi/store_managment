package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CustomerBuyerView extends JFrame {

    private JButton btnAdd = new JButton("Add a new item");
    private JButton btnPay = new JButton("Finish and Pay");
    private JButton btnLoadProducts = new JButton("Load Products");

    private DefaultTableModel items = new DefaultTableModel(); // store information for the table!

    private JTable tblItems = new JTable(items); // null, new String[]{"ProductID", "Product Name", "Price", "Quantity", "Cost"});
    private JLabel labTotal = new JLabel("Total: ");

    public CustomerBuyerView() {

        this.setTitle("Checkout");
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.setSize(400, 600);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        items.addColumn("Product ID");
        items.addColumn("Name");
        items.addColumn("Price");
        items.addColumn("Quantity");
        items.addColumn("Cost");

        JPanel panelOrder = new JPanel();
        panelOrder.setPreferredSize(new Dimension(400, 450));
        panelOrder.setLayout(new BoxLayout(panelOrder, BoxLayout.PAGE_AXIS));
        tblItems.setBounds(0, 0, 400, 350);
        panelOrder.add(tblItems.getTableHeader());
        panelOrder.add(tblItems);
        panelOrder.add(labTotal);
        tblItems.setFillsViewportHeight(true);
        this.getContentPane().add(panelOrder);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Panel for the new button
        topPanel.add(btnLoadProducts);  // Add the button to the panel
        this.getContentPane().add(topPanel, BorderLayout.NORTH); // Add the panel to the top of the frame

        JPanel panelButton = new JPanel();
        panelButton.setPreferredSize(new Dimension(400, 100));
        panelButton.add(btnAdd);
        panelButton.add(btnPay);
        this.getContentPane().add(panelButton);

    }

    public JButton getBtnAdd() {
        return btnAdd;
    }

    public JButton getBtnPay() {
        return btnPay;
    }

    public JLabel getLabTotal() {
        return labTotal;
    }

    public JButton getBtnLoadProducts() {
        return btnLoadProducts;
    }

    public void addRow(Object[] row) {
        items.addRow(row);              // add a row to list of item!
    //    items.fireTableDataChanged();
    }
    public void clearOrder(){
        DefaultTableModel model = (DefaultTableModel) tblItems.getModel();
        model.setRowCount(0); // clear table
        getLabTotal().setText("Total: $0.0");
    }
}
