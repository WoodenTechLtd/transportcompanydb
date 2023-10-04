package com.transportSys.orderdetails;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import com.transportSys.connection.*;

public class OrderDetailsGUI extends JFrame {

    private JTextField orderNumberField;
    private JTextField productCodeField;
    private JTextField orderLineNumberField;
    private JTable table;
    private DefaultTableModel tableModel;

    public OrderDetailsGUI() {
        setTitle("Order Details Manager");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel searchPanel = new JPanel();
        orderNumberField = new JTextField(10);
        productCodeField = new JTextField(10);
        orderLineNumberField = new JTextField(10);

        orderNumberField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchOrderDetails();
            }
        });

        productCodeField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchOrderDetails();
            }
        });

        orderLineNumberField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchOrderDetails();
            }
        });

        // Create the table with column names
        Vector<String> columnNames = new Vector<>();
        columnNames.add("Order Number");
        columnNames.add("Product Code");
        columnNames.add("Order Line Number");
        columnNames.add("Quantity Ordered");
        columnNames.add("Price Each");
        columnNames.add("Order Identifier"); // Add a column for the unique identifier

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow editing of quantity ordered column
                return column == 3;
            }
        };

        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Add a context menu for editing orders
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem editOrderItem = new JMenuItem("Edit Order");

        editOrderItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String currentOrderNumber = table.getValueAt(selectedRow, 0).toString();
                    String currentQuantity = table.getValueAt(selectedRow, 3).toString();
                    String currentPrice = table.getValueAt(selectedRow, 4).toString();
                    String currentOrderLineNumber = table.getValueAt(selectedRow, 2).toString();

                    JTextField orderNumberField = new JTextField(currentOrderNumber);
                    orderNumberField.setEditable(false); // Make order number non-editable
                    JTextField quantityField = new JTextField(currentQuantity);
                    JTextField priceField = new JTextField(currentPrice);
                    JTextField orderLineNumberField = new JTextField(currentOrderLineNumber);

                    Object[] fields = {
                        "Order Number:", orderNumberField,
                        "Quantity Ordered:", quantityField,
                        "Price Each:", priceField,
                        "Order Line Number:", orderLineNumberField
                    };

                    int result = JOptionPane.showConfirmDialog(null, fields, "Edit Order",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    if (result == JOptionPane.OK_OPTION) {
                        int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to edit this order?", "Confirm Edit", JOptionPane.YES_NO_OPTION);
                        if (confirmation == JOptionPane.YES_OPTION) {
                            String newQuantity = quantityField.getText();
                            String newPrice = priceField.getText();
                            String newOrderLineNumber = orderLineNumberField.getText();

                            String oldData = "Old Data:\n"
                                    + "Order Number: " + currentOrderNumber + "\n"
                                    + "Quantity Ordered: " + currentQuantity + "\n"
                                    + "Price Each: " + currentPrice + "\n"
                                    + "Order Line Number: " + currentOrderLineNumber + "\n";

                            String newData = "New Data:\n"
                                    + "Quantity Ordered: " + newQuantity + "\n"
                                    + "Price Each: " + newPrice + "\n"
                                    + "Order Line Number: " + newOrderLineNumber + "\n";

                            String editMessage = oldData + "\n" + newData;
                            JOptionPane.showMessageDialog(null, editMessage, "Edit Confirmation", JOptionPane.INFORMATION_MESSAGE);

                            table.setValueAt(newQuantity, selectedRow, 3);
                            table.setValueAt(newPrice, selectedRow, 4);
                            table.setValueAt(newOrderLineNumber, selectedRow, 2);

                            String uniqueIdentifier = table.getValueAt(selectedRow, 5).toString(); // Get the unique identifier
                            updateOrderInDatabase(uniqueIdentifier, newQuantity, newPrice, newOrderLineNumber); // Update the order in the database
                        }
                    }
                }
            }
        });

        contextMenu.add(editOrderItem);
        table.setComponentPopupMenu(contextMenu);

        setLayout(new BorderLayout());
        searchPanel.add(new JLabel("Order Number:"));
        searchPanel.add(orderNumberField);
        searchPanel.add(new JLabel("Product Code:"));
        searchPanel.add(productCodeField);
        searchPanel.add(new JLabel("Order Line Number:"));
        searchPanel.add(orderLineNumberField);
        add(searchPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Load all order details initially
        loadAllOrderDetails();
    }

    private void loadAllOrderDetails() {
        try (Connection connection = dbConnection.getConnection()) {
            String query = "SELECT * FROM orderdetails";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            ResultSet resultSet = preparedStatement.executeQuery();

            // Populate the table with all order details
            tableModel.setRowCount(0); // Clear previous data
            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                row.add(resultSet.getInt("orderNumber"));
                row.add(resultSet.getString("productCode"));
                row.add(resultSet.getInt("orderLineNumber"));
                row.add(resultSet.getInt("quantityOrdered"));
                row.add(resultSet.getDouble("priceEach"));
                String orderIdentifier = resultSet.getInt("orderNumber") + "-" +
                        resultSet.getString("productCode") + "-" +
                        resultSet.getInt("orderLineNumber");
                row.add(orderIdentifier); // Store the unique identifier
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage());
        }
    }

    private void searchOrderDetails() {
        String orderNumber = orderNumberField.getText();
        String productCode = productCodeField.getText();
        String orderLineNumber = orderLineNumberField.getText();

        if (orderNumber.isEmpty() && productCode.isEmpty() && orderLineNumber.isEmpty()) {
            // If all fields are empty, load all order details
            loadAllOrderDetails();
        } else {
            try (Connection connection = dbConnection.getConnection()) {
                String query = "SELECT * FROM orderdetails WHERE (orderNumber LIKE ? OR ? = '') AND (productCode LIKE ? OR ? = '') AND (orderLineNumber = ? OR ? = '')";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, "%" + orderNumber + "%");
                preparedStatement.setString(2, orderNumber);
                preparedStatement.setString(3, "%" + productCode + "%");
                preparedStatement.setString(4, productCode);
                preparedStatement.setString(5, orderLineNumber);
                preparedStatement.setString(6, orderLineNumber);

                ResultSet resultSet = preparedStatement.executeQuery();

                // Populate the table with the search results
                tableModel.setRowCount(0); // Clear previous data
                while (resultSet.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(resultSet.getInt("orderNumber"));
                    row.add(resultSet.getString("productCode"));
                    row.add(resultSet.getInt("orderLineNumber"));
                    row.add(resultSet.getInt("quantityOrdered"));
                    row.add(resultSet.getDouble("priceEach"));
                    String orderIdentifier = resultSet.getInt("orderNumber") + "-" +
                            resultSet.getString("productCode") + "-" +
                            resultSet.getInt("orderLineNumber");
                    row.add(orderIdentifier); // Store the unique identifier
                    tableModel.addRow(row);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage());
            }
        }
    }

    // Method to update an order in the database
    private void updateOrderInDatabase(String uniqueIdentifier, String newQuantity, String newPrice, String newOrderLineNumber) {
        // Parse the unique identifier and split it into its components
        String[] identifierParts = uniqueIdentifier.split("-");
        int orderNumber = Integer.parseInt(identifierParts[0]);
        String productCode = identifierParts[1];
        int orderLineNumber = Integer.parseInt(identifierParts[2]);

        try (Connection connection = dbConnection.getConnection()) {
            String query = "UPDATE orderdetails SET quantityOrdered = ?, priceEach = ?, orderLineNumber = ? WHERE orderNumber = ? AND productCode = ? AND orderLineNumber = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newQuantity);
            preparedStatement.setString(2, newPrice);
            preparedStatement.setString(3, newOrderLineNumber);
            preparedStatement.setInt(4, orderNumber);
            preparedStatement.setString(5, productCode);
            preparedStatement.setInt(6, orderLineNumber);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "An error occurred while updating the order in the database: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new OrderDetailsGUI().setVisible(true);
            }
        });
    }
}
