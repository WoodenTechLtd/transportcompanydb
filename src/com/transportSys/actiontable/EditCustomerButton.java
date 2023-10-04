package com.transportSys.actiontable;


import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class EditCustomerButton extends JButton {
    private JTable table;
    private String dbUrl;
    private String username;
    private String password;
    private String tableName;
    private String primaryKeyColumnName;


    public EditCustomerButton(String text, JTable table, String primaryKeyColumnName, String tableName,
                              String dbUrl, String username, String password) {
        super(text);
        this.table = table;
        this.primaryKeyColumnName = primaryKeyColumnName;
        this.tableName = tableName;
        this.dbUrl = dbUrl;
        this.username = username;
        this.password = password;
        addActionListener(new EditCustomerActionListener());
    }

    private class EditCustomerActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                Map<String, Object> rowData = new HashMap<>();

                // Populate rowData with the values from the selected row in the JTable
                for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
                    String columnName = table.getColumnName(columnIndex);
                    Object columnValue = table.getValueAt(selectedRow, columnIndex);

                    // Handle null values by converting them to empty strings for display
                    String columnValueStr = (columnValue != null) ? columnValue.toString() : "";

                    rowData.put(columnName, columnValueStr);
                }

                // Create text fields for editing
                Map<String, JTextField> columnTextFields = new HashMap<>();
                JPanel panel = new JPanel(new GridLayout(0, 2)); // 2 columns

                for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
                    String columnName = table.getColumnName(columnIndex);
                    String columnValue = (String) rowData.get(columnName);

                    JLabel label = new JLabel(columnName);
                    JTextField textField = new JTextField(columnValue);

                    // Make the primary key field uneditable
                    if (columnName.equals(primaryKeyColumnName)) {
                        textField.setEditable(false);
                    }

                    columnTextFields.put(columnName, textField);

                    panel.add(label);
                    panel.add(textField);
                }

                // Show the dialog to edit customer information
                int result = JOptionPane.showConfirmDialog(null, panel, "Edit Customer",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    // Extract values from text fields and update the table
                    for (Entry<String, JTextField> entry : columnTextFields.entrySet()) {
                        String columnName = entry.getKey();
                        JTextField textField = entry.getValue();
                        String value = textField.getText();

                        // Handle converting empty strings back to null for integer columns
                        Object parsedValue = value.isEmpty() ? null : value;

                        rowData.put(columnName, parsedValue);
                    }

                    // Extract primary key (customerNumber)
                    int primaryKeyValue = Integer.parseInt(rowData.get(primaryKeyColumnName).toString());

                    // Update the table with new data
                    for (int i = 0; i < table.getColumnCount(); i++) {
                        String columnName = table.getColumnName(i);
                        Object value = rowData.get(columnName);
                        table.setValueAt(value, selectedRow, i);
                    }

                    // Update the customer data in the database (call your update method here)
                    updateData(tableName, primaryKeyColumnName, primaryKeyValue, rowData);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a row to edit.");
            }
        }
    }

    // Define your updateData method here
    private void updateData(String tableName, String primaryKeyColumnName, int primaryKeyValue,
                            Map<String, Object> columnData) {
        Connection myConn = null;
        PreparedStatement pst = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            myConn = DriverManager.getConnection(dbUrl, username, password);

            StringBuilder queryBuilder = new StringBuilder("UPDATE ");
            queryBuilder.append(tableName);
            queryBuilder.append(" SET ");

            // Create a list to hold the column names for the SET clause
            for (String columnName : columnData.keySet()) {
                queryBuilder.append(columnName).append("=?, ");
            }
            queryBuilder.setLength(queryBuilder.length() - 2); // Remove the trailing comma and space
            queryBuilder.append(" WHERE ").append(primaryKeyColumnName).append("=?");

            pst = myConn.prepareStatement(queryBuilder.toString());

            // Set values for the columns
            int parameterIndex = 1;
            for (Object columnValue : columnData.values()) {
                // Check if the columnValue is null and set it as INTEGER type
                if (columnValue == null) {
                    pst.setNull(parameterIndex++, java.sql.Types.INTEGER);
                } else {
                    pst.setObject(parameterIndex++, columnValue);
                }
            }

            // Set the primary key value
            pst.setInt(parameterIndex, primaryKeyValue);

            // Execute the update query
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                myConn.setAutoCommit(false);
                myConn.commit();
                myConn.setAutoCommit(true);
                JOptionPane.showMessageDialog(null, "Data saved successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "No data updated. Please check your inputs.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            // Handle exceptions
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error updating data: " + e.getMessage());
        } finally {
            // Close resources
            try {
                if (pst != null) {
                    pst.close();
                }
                if (myConn != null) {
                    myConn.close();
                }
            } catch (SQLException e) {
                // Handle exceptions while closing
                e.printStackTrace();
            }
        }
    }
}