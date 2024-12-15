package view;

import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ManageShippersView extends JFrame {
    private final JPanel inputsPanel, tablePanel;
    private final JPanel bottomPanel;
    private final JLabel shipperIDLabel, companyNameLabel, pricePerKMLabel;
    private final JTextField shipperIDTextField, companyNameTextField, pricePerKMTextField;
    private final JButton createShipperButton, updateShipperButton, deleteShipperButton;
    private final Dimension labelDimension = new Dimension(90, 20), inputBoxDimension = new Dimension(180, 20),
            inputPanelDimension = new Dimension((int)(labelDimension.getWidth() + inputBoxDimension.getWidth()) + 20, 0),
            tableDimension = new Dimension(600, 700), buttonsDimension = new Dimension(105, 25);
    private final DefaultTableModel tableModel;
    private final JTable shipperDataTable;
    private final JScrollPane scrollPane;
    private Object[][] shipperData;
    private final String[] tableColumns;
    private final Color mainColor = Color.white, inputColor = Color.black;

    public ManageShippersView() {
        /****************************** Frame ******************************/
        this.setTitle("Manage Shippers");
        this.setSize(1028, 800);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        inputsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        inputsPanel.setPreferredSize(inputPanelDimension);
        inputsPanel.setBackground(mainColor);
        inputsPanel.setBackground(Color.WHITE);
        this.add(inputsPanel, BorderLayout.WEST);

        tablePanel = new JPanel(new GridBagLayout());
        tablePanel.setBackground(mainColor);
        this.add(tablePanel, BorderLayout.CENTER);
        /****************************** Frame ******************************/
        /****************************** Input ******************************/

        shipperIDLabel = new JLabel("Shipper ID");
        shipperIDTextField = new JTextField();
        setTextFieldDesign(shipperIDLabel, shipperIDTextField);

        companyNameLabel = new JLabel("Company Name");
        companyNameTextField = new JTextField();
        setTextFieldDesign(companyNameLabel, companyNameTextField);

        pricePerKMLabel = new JLabel("Price Per KM");
        pricePerKMTextField = new JTextField();
        setTextFieldDesign(pricePerKMLabel, pricePerKMTextField);

        createShipperButton = new JButton("Create Shipper");
        setButtonDesign(createShipperButton);
        inputsPanel.add(createShipperButton);

        updateShipperButton = new JButton("Update Shipper");
        setButtonDesign(updateShipperButton);
        inputsPanel.add(updateShipperButton);

        deleteShipperButton = new JButton("Delete Shipper");
        setButtonDesign(deleteShipperButton);
        inputsPanel.add(deleteShipperButton);
        /****************************** Input ******************************/

        tableColumns = new String[]{"Shipper ID", "Company Name", "Price Per KM"};
        tableModel = new DefaultTableModel(shipperData, tableColumns);

        shipperDataTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        shipperDataTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2 && event.getButton() == MouseEvent.BUTTON1) {
                    int selectedRow = shipperDataTable.getSelectedRow();
                    if (selectedRow != -1) {
                        shipperIDTextField.setText((String) shipperDataTable.getValueAt(selectedRow, 0));
                        companyNameTextField.setText((String) shipperDataTable.getValueAt(selectedRow, 1));
                        pricePerKMTextField.setText((String) shipperDataTable.getValueAt(selectedRow, 2));
                    }
                }
            }
        });

        scrollPane = new JScrollPane(shipperDataTable);
        scrollPane.setPreferredSize(tableDimension);
        tablePanel.add(scrollPane, new GridBagConstraints());

        bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 5));
        bottomPanel.setPreferredSize(new Dimension(0, 50));
        bottomPanel.setBackground(mainColor);
        this.add(bottomPanel, BorderLayout.SOUTH);

    }

    private void setTextFieldDesign(JLabel label, JTextField textField) {
        label.setPreferredSize(labelDimension);
        label.setFont(new Font("Calibri", Font.BOLD, 14));
        inputsPanel.add(label);

        textField.setPreferredSize(inputBoxDimension);
        textField.setForeground(inputColor);
        textField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, inputColor));
        inputsPanel.add(textField);
    }

    private void setButtonDesign(JButton button) {
        button.setPreferredSize(buttonsDimension);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createLineBorder(inputColor));
        button.setBackground(mainColor);
        button.setForeground(inputColor);
    }

    public JTextField getShipperIDTextField() {
        return shipperIDTextField;
    }

    public JTextField getCompanyNameTextField() {
        return companyNameTextField;
    }

    public JTextField getPricePerKMTextField() {
        return pricePerKMTextField;
    }

    public JButton getCreateShipperButton() {
        return createShipperButton;
    }

    public JButton getUpdateShipperButton() {
        return updateShipperButton;
    }

    public JButton getDeleteShipperButton() {
        return deleteShipperButton;
    }

    public void setShipperDataTable(String[][] shipperData) {
        this.shipperData = shipperData;
    }

    public void updateTable() {
        tableModel.setDataVector(shipperData, tableColumns);
    }

    public void clearInputBoxes() {
        shipperIDTextField.setText("");
        companyNameTextField.setText("");
        pricePerKMTextField.setText("");
    }

    public JSONObject getShipperPayload() {
        try {
            int shipperID = Integer.parseInt(shipperIDTextField.getText());
            String companyName = companyNameTextField.getText();
            double pricePerKM = Double.parseDouble(pricePerKMTextField.getText());

            if (companyName.isEmpty()) {
                throw new IllegalArgumentException("Company Name cannot be empty.");
            }

            JSONObject shipperPayload = new JSONObject();
            shipperPayload.put("shipperID", shipperID);
            shipperPayload.put("companyName", companyName);
            shipperPayload.put("pricePerKM", pricePerKM);

            return shipperPayload;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check the fields.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null; // Return null if there is an error
    }

}
