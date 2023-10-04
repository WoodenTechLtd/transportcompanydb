package com.transportSys.products;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

import com.transportSys.connection.dbConnection; // Importing the dbConnection class
import java.awt.Font;
import java.awt.Color; // Importing the dbConnection class

public class productsWindow {
	
    private JFrame frame;
    private JTable table;
    private JScrollPane scrollPane;

    //search box field
    private JTextField productCodeField;
    private JTextField productNameField;
    private JTextField productLineField;
    private JTextField productVendorField;
    private JLabel commentLabel;
    
    /**
     * @wbp.parser.entryPoint
     */
    public productsWindow() {
    	 	frame = new JFrame("Products Table");
    	    frame.setSize(900, 650);
    	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	    frame.setLocationRelativeTo(null);

    	    frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));



    	    JPanel searchPanel = new JPanel(new FlowLayout());
    	    searchPanel.setBackground(new Color(75, 46, 10));
    	    searchPanel.setForeground(new Color(250, 243, 224));
    	    frame.getContentPane().add(searchPanel);
    	    
            JPanel textPanel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("*Right click on a product to copy / edit / delete*");
            textPanel.add(label, BorderLayout.EAST);
    	    

            // ... [Setup your JFrame settings]

                commentLabel = new JLabel("Right click on a product to copy / edit / delete");
                commentLabel.setBounds(10, 31, 309, 19);
                commentLabel.setVerticalAlignment(SwingConstants.BOTTOM);
                commentLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
                
                // Create a panel to hold the label, aligned to the left
                JPanel labelPanel = new JPanel();
                labelPanel.setBackground(new Color(250, 243, 224));    // Beige
                labelPanel.setForeground(new Color(250, 243, 224));
                labelPanel.setLayout(null);
                labelPanel.add(commentLabel);

                // Add the label panel to the NORTH (top) section of the container
                frame.getContentPane().add(labelPanel, BorderLayout.NORTH);

                // Add padding around the container
                
            
        // Add the search fields
    	    productCodeField = new JTextField(10);
    	    productNameField = new JTextField(10);
    	    productLineField = new JTextField(10);
    	    productVendorField = new JTextField(10);

    	    // KeyListener to update the table as you type
    	    KeyAdapter searchOnKeyRelease = new KeyAdapter() {
    	        public void keyReleased(KeyEvent e) {
    	            searchProducts(); // this assumes the searchProducts() method updates the table based on the search fields
    	        }
    	    };

    	    // Add the KeyListener to the search fields
    	    productCodeField.addKeyListener(searchOnKeyRelease);
    	    productNameField.addKeyListener(searchOnKeyRelease);
    	    productLineField.addKeyListener(searchOnKeyRelease);
    	    productVendorField.addKeyListener(searchOnKeyRelease);

    	    JLabel label_1 = new JLabel("Product Code:");
    	    label_1.setForeground(new Color(250, 243, 224));
    	    searchPanel.add(label_1);
    	    searchPanel.add(productCodeField);
    	    JLabel label_2 = new JLabel("Product Name:");
    	    label_2.setForeground(new Color(250, 243, 224));
    	    searchPanel.add(label_2);
    	    searchPanel.add(productNameField);
    	    JLabel label_3 = new JLabel("Product Line:");
    	    label_3.setForeground(new Color(250, 243, 224));
    	    searchPanel.add(label_3);
    	    searchPanel.add(productLineField);
    	    JLabel label_4 = new JLabel("Product Vendor:");
    	    label_4.setForeground(new Color(250, 243, 224));
    	    searchPanel.add(label_4);
    	    searchPanel.add(productVendorField);

    	    JButton searchButton = new JButton("Search");
    	    searchButton.addActionListener(e -> searchProducts());
    	    searchPanel.add(searchButton);
    	    // enter triggers search
    	    frame.getRootPane().setDefaultButton(searchButton);

    	    JButton addButton = new JButton("Add Product");
    	    addButton.addActionListener(e -> showAddProductWindow());
    	    searchPanel.add(addButton);

    	    String[] columnNames = {"Product Code", "Product Name", "Product Line", "Product Scale", "Product Vendor", "Product Description", "Quantity In Stock", "Buy Price", "MSRP"};

    	    DefaultTableModel model = new DefaultTableModel();
    	    model.setColumnIdentifiers(columnNames);

    	 // Earthy Tone Colors
    	    table = new JTable();
    	    table.setForeground(new Color(75, 46, 10)); // Dark Brown
    	    table.setBackground(new Color(250, 243, 224));    // Beige
    	    table.setModel(model);
    	    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    	    table.setFillsViewportHeight(true);

    	    // Set the selection background to Tan
    	    table.setSelectionBackground(new Color(229, 192, 149)); // Tan

    	    // Set the selection foreground (text color) to Dark Brown
    	    table.setSelectionForeground(new Color(75, 46, 10)); // Dark Brown

    	    // Get the table header
    	    JTableHeader header = table.getTableHeader();

    	    // Set the background color of the header to Sienna
    	    header.setBackground(new Color(160, 82, 45));  // Sienna

    	    // Set the foreground color (text color) of the header to Dark Brown
    	    header.setForeground(new Color(250, 243, 224));  // Dark Brown





    	    
        scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);  // <-- Moved this line down
        populateTable(model);
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                int r = table.rowAtPoint(e.getPoint());
                int c = table.columnAtPoint(e.getPoint());
                if (r >= 0 && r < table.getRowCount()) {
                    table.setRowSelectionInterval(r, r);
                } else {
                    table.clearSelection();
                }

                int rowindex = table.getSelectedRow();
                if (rowindex < 0)
                    return;

                if (e.getClickCount() == 1 && c == 5) { // Adjusted for column index 5 (0-based index)
                    showDescriptionPopup(rowindex, c);
                } else if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    JPopupMenu popup = createRightClickMenu();
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        frame.setVisible(true);

    }

    // tooltip for description column
    
    private void showDescriptionPopup(int row, int col) {
        String description = (String) table.getValueAt(row, col);
        String productCode = (String) table.getValueAt(row, 0);  // Assuming the productCode is in column 0

        Window parentWindow = SwingUtilities.windowForComponent(table);
        JDialog descriptionDialog = new JDialog(parentWindow, "Description", ModalityType.MODELESS);
        JTextArea descriptionArea = new JTextArea(description);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        
        // Wrap scrollPane in a JPanel with padding
        JPanel paddedPanel = new JPanel(new BorderLayout());
        paddedPanel.add(scrollPane, BorderLayout.CENTER);
        int padding = 10;  // 10 pixels padding
        paddedPanel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(descriptionDialog, "Are you sure you want to save?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        updateProductDescription(productCode, descriptionArea.getText()); // update in the database
                        table.setValueAt(descriptionArea.getText(), row, col); // update in the JTable
                        descriptionDialog.dispose();  // close the dialog
                    } catch(SQLException ex) {
                        JOptionPane.showMessageDialog(descriptionDialog, "Failed to update the description in the database.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        descriptionDialog.getContentPane().setLayout(new BorderLayout());
        descriptionDialog.getContentPane().add(paddedPanel, BorderLayout.CENTER);  // Add the padded panel instead of directly adding the scrollPane
        descriptionDialog.getContentPane().add(saveButton, BorderLayout.SOUTH);
        descriptionDialog.setSize(300, 300);  // Set the size of the dialog directly
        descriptionDialog.setLocationRelativeTo(null);
        descriptionDialog.setVisible(true);
    }


    private void updateProductDescription(String productCode, String newDescription) throws SQLException {
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();

            String updateQuery = "UPDATE products SET productDescription = ? WHERE productCode = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(updateQuery);
            preparedStatement.setString(1, newDescription);
            preparedStatement.setString(2, productCode);

            preparedStatement.executeUpdate();
        } finally {
            dbConnection.closeConnection(conn);
        }
    }



    
    
//  Maps the display column name from the JTable to the corresponding column name in the database.
    private String mapDisplayNameToDBColumn(String displayName) {
        switch (displayName) {
            case "Product Code":
                return "productCode";
            case "Product Name":
                return "productName";
            case "Product Line":
                return "productLine";
            case "Product Scale":
                return "productScale";
            case "Product Vendor":
                return "productVendor";
            case "Product Description":
                return "productDescription";
            case "Quantity In Stock":
                return "quantityInStock";
            case "Buy Price":
                return "buyPrice";
            case "MSRP":
                return "MSRP";
            default:
                throw new IllegalArgumentException("Invalid display column name: " + displayName);
        }
    }

 // ------------- [ drop down menu of product lists ] -------------
    private List<String> fetchProductLines() {
        List<String> productLines = new ArrayList<>();
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            // Updated the query to pull data from the productlines table
            String query = "SELECT productLine FROM productlines";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                productLines.add(resultSet.getString("productLine"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return productLines;
    }

 // check if product list is selected to be edited:   
    
    private boolean productLineExists(String productLine) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean exists = false;
        
        String sql = "SELECT COUNT(*) FROM productlines WHERE productLine = ?";

        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, productLine);

            rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                exists = true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbConnection.closeConnection(conn);
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return exists;
    }

    
 // popup window with options after righ click 
    
    private JPopupMenu createRightClickMenu() {
        JPopupMenu menu = new JPopupMenu();

     // ------------- [ Copy Column ] -------------
        
        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.addActionListener(e -> {
            int selectedColumn = table.getSelectedColumn();
            int selectedRow = table.getSelectedRow();
            Object value = table.getValueAt(selectedRow, selectedColumn);
            StringSelection stringSelection = new StringSelection(value.toString());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        });
        menu.add(copyItem);


     // ------------- | Editing row option + confirm window | -------------

        JMenuItem editRowItem = new JMenuItem("Edit Row");
        editRowItem.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();

            if (selectedRow != -1) {
                // Open a popup for editing
                JFrame editFrame = new JFrame("Edit Row Values");
                editFrame.setLayout(new GridLayout(0, 2));  // For labels and text fields

                Map<String, JTextField> fieldsMap = new HashMap<>();  // To hold column names and corresponding text fields

                for (int i = 0; i < table.getColumnCount(); i++) {
                    String columnName = table.getColumnName(i);
                    Object currentValue = table.getValueAt(selectedRow, i);

                    JLabel label = new JLabel(columnName + ": ");
                    JTextField editField = new JTextField(20);
                    editField.setText(currentValue.toString());

                    fieldsMap.put(columnName, editField);  // Storing in map

                    editFrame.add(label);
                    editFrame.add(editField);
                }

                JButton saveButton = new JButton("Save");
                editFrame.add(new JLabel(""));  // Placeholder
                editFrame.add(saveButton);

                saveButton.addActionListener(innerEvent -> {
                    // Store changes and verify if there are any modifications
                    StringBuilder changes = new StringBuilder("<html>Changes:<br>");
                    boolean hasChanges = false;

                    for (int i = 0; i < table.getColumnCount(); i++) {
                        String columnName = table.getColumnName(i);
                        JTextField editField = fieldsMap.get(columnName);

                        String newValue = editField.getText();
                        String oldValue = table.getValueAt(selectedRow, i).toString();

                        if (!oldValue.equals(newValue)) {
                            hasChanges = true;
                            changes.append(columnName).append(": ").append(oldValue).append(" ⇶ ").append(newValue).append("<br>");
                        }
                    }

                    changes.append("</html>");

                    if (hasChanges) {
                        // Show the before and after changes in a confirmation window
                        int response = JOptionPane.showConfirmDialog(
                            null,
                            changes.toString(),
                            "Confirm Changes",
                            JOptionPane.YES_NO_OPTION
                        );

                        if (response == JOptionPane.YES_OPTION) {
                            for (int i = 0; i < table.getColumnCount(); i++) {
                                String columnName = table.getColumnName(i);
                                JTextField editField = fieldsMap.get(columnName);
                                String newValue = editField.getText();

                                // Update the table model
                                table.setValueAt(newValue, selectedRow, i);

                                // Assuming you have a method to map displayed column names to DB names
                                String dbColumnName = mapDisplayNameToDBColumn(columnName);

                                // Save the updated value to the database
                                String productCode = table.getValueAt(selectedRow, 0).toString();
                                updateProductInDatabase(productCode, dbColumnName, newValue);
                            }
                        }
                    }

                    // Close the edit frame
                    editFrame.dispose();
                });

                editFrame.pack();
                editFrame.setLocationRelativeTo(null);
                editFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "No row selected!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        menu.add(editRowItem);
       
        
        // ------------- | Deleting row option + confirm window | -------------
        
        JMenuItem deleteRowItem = new JMenuItem("Delete Row");
        deleteRowItem.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) { // Checking if a row is actually selected
                String productCode = table.getValueAt(selectedRow, 0).toString(); // Assuming product code is always in the first column
                String productName = table.getValueAt(selectedRow, 1).toString(); // Assuming product name is in the second column
                
                // Displaying the confirmation dialog
                int response = JOptionPane.showConfirmDialog(
                    null, 
                    "Are you sure you want to remove " + productCode + " — " + productName + "?", 
                    "Confirm Removal", 
                    JOptionPane.YES_NO_OPTION
                );
                
                if (response == JOptionPane.YES_OPTION) {
                    deleteProductFromDatabase(productCode);
                    ((DefaultTableModel) table.getModel()).removeRow(selectedRow);
                }
            } else {
                JOptionPane.showMessageDialog(null, "No row selected!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        menu.add(deleteRowItem);


        return menu;
    }

    
 // ------------- [ update method to save the edit in database ] -------------
    
    private void updateProductInDatabase(String productCode, String columnName, String newValue) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        // Whitelist column names to prevent SQL injection
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("productCode", "productName", "productLine", "productScale", "productVendor", "productDescription", "quantityInStock", "buyPrice", "MSRP")); // Add your column names here

        if (!allowedColumns.contains(columnName)) {
            JOptionPane.showMessageDialog(null, "Invalid column name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "UPDATE products SET `" + columnName + "` = ? WHERE productCode = ?";

        try {
            
        	conn = dbConnection.getConnection();
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newValue);
            pstmt.setString(2, productCode);

            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Error updating product in database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        	} finally {
        		dbConnection.closeConnection(conn);
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
        }
    }


    // ------------- [ delete method to save the delete in database ] -------------

	private void deleteProductFromDatabase(String productCode) {
        // Logic to delete product from the database using the productCode
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            String deleteQuery = "DELETE FROM products WHERE productCode = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(deleteQuery);
            preparedStatement.setString(1, productCode);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "An error occurred while deleting the product.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            dbConnection.closeConnection(conn);
        }
    }

    
 // search method


    private void searchProducts() {
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            
            StringBuilder query = new StringBuilder("SELECT * FROM products WHERE 1=1"); 

            List<String> parameters = new ArrayList<>();

            if (!productCodeField.getText().trim().isEmpty()) {
                query.append(" AND productCode LIKE ?");
                parameters.add("%" + productCodeField.getText().trim() + "%");
            }

            if (!productNameField.getText().trim().isEmpty()) {
                query.append(" AND productName LIKE ?");
                parameters.add("%" + productNameField.getText().trim() + "%");
            }

            if (!productLineField.getText().trim().isEmpty()) {
                query.append(" AND productLine LIKE ?");
                parameters.add("%" + productLineField.getText().trim() + "%");
            }

            if (!productVendorField.getText().trim().isEmpty()) {
                query.append(" AND productVendor LIKE ?");
                parameters.add("%" + productVendorField.getText().trim() + "%");
            }

            PreparedStatement preparedStatement = conn.prepareStatement(query.toString());

            // Set the parameters for the prepared statement
            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setString(i + 1, parameters.get(i));
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0); // Clear the table first

            // Display following columns when one uses either of the search functions
            while (resultSet.next()) {
                String productCode = resultSet.getString("productCode");
                String productName = resultSet.getString("productName");
                String productLine = resultSet.getString("productLine");
                String productScale = resultSet.getString("productScale");
                String productVendor = resultSet.getString("productVendor");
                String productDescription = resultSet.getString("productDescription");
                String quantityInStock = resultSet.getString("quantityInStock");
                String buyPrice = resultSet.getString("buyPrice");
                String MSRP = resultSet.getString("MSRP");

                model.addRow(new Object[]{productCode, productName, productLine, productScale, productVendor, productDescription, quantityInStock, buyPrice, MSRP});
            }
            

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            dbConnection.closeConnection(conn);
        }
    }





 // Declaring it at the class level so it becomes a field of the class
    private JComboBox<String> productLineDropdown = new JComboBox<>();
   
    // addProductButton method
    private void showAddProductWindow() {
    	   JFrame addFrame = new JFrame("Add Product");
    	    addFrame.setSize(300, 450);
    	    addFrame.revalidate();
    	    addFrame.repaint();

    	    addFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	    addFrame.setLocationRelativeTo(null);  // Centers the window on screen

    	    JPanel panel = new JPanel();
    	    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    	    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Some padding

    	    JTextField productCodeInput = new JTextField(10); // Width for regular fields
    	    JTextField productNameInput = new JTextField(10);
    	    //drop down menu instead of direct input for product lists
    	    JComboBox<String> productLineDropdown = new JComboBox<>();
    	    List<String> productLines = fetchProductLines();
    	    for (String line : productLines) {
    	        productLineDropdown.addItem(line);
    	    }
    	    JTextField productScaleInput = new JTextField(10);
    	    JTextField productVendorInput = new JTextField(10);
    	    JTextArea productDescriptionInput = new JTextArea(3, 20); // A JTextArea with 3 rows and 20 columns for larger descriptions
    	    productDescriptionInput.setWrapStyleWord(true); // wrap lines at word boundaries
    	    productDescriptionInput.setLineWrap(true); // enable line wrapping
    	    JScrollPane productDescriptionScrollPane = new JScrollPane(productDescriptionInput); // Scroll pane for the description
    	    JTextField quantityInStockInput = new JTextField(10);
    	    JTextField buyPriceInput = new JTextField(10);
    	    JTextField MSRPInput = new JTextField(10);


    	    
    	    JButton saveButton = new JButton("Add Product");
    	    saveButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the button
    	    saveButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, saveButton.getMinimumSize().height)); // Make button fill the width

    	    saveButton.addActionListener(e -> {
            try {
                // Validate if any field is empty
                if (productCodeInput.getText().trim().isEmpty() || 
                    productNameInput.getText().trim().isEmpty() ||
                    // productLineInput.getText().trim().isEmpty() ||
                    productScaleInput.getText().trim().isEmpty() ||
                    productVendorInput.getText().trim().isEmpty() ||
                    productDescriptionInput.getText().trim().isEmpty() ||
                    quantityInStockInput.getText().trim().isEmpty() ||
                    buyPriceInput.getText().trim().isEmpty() ||
                    MSRPInput.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(addFrame, "All fields must be filled out.", "Incomplete Form", JOptionPane.ERROR_MESSAGE);
                    return;  // Exit the method early
                }

                String selectedProductLine = (String) productLineDropdown.getSelectedItem();

                addProduct(
                    productCodeInput.getText(),
                    productNameInput.getText(),
                    selectedProductLine,
                    productScaleInput.getText(),
                    productVendorInput.getText(),
                    productDescriptionInput.getText(),
                    Integer.parseInt(quantityInStockInput.getText()), 
                    Double.parseDouble(buyPriceInput.getText()), 
                    Double.parseDouble(MSRPInput.getText()) 
                );


                JOptionPane.showMessageDialog(addFrame, "Product successfully added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                productCodeInput.setText("");
                productNameInput.setText("");
                productLineDropdown.removeAllItems();
                productLineDropdown.addItem("");

                productScaleInput.setText("");
                productVendorInput.setText("");
                productDescriptionInput.setText("");
                quantityInStockInput.setText("");
                buyPriceInput.setText("");
                MSRPInput.setText("");

            } catch (SQLIntegrityConstraintViolationException fkException) {
                if (fkException.getMessage().contains("productLine")) {
                    JOptionPane.showMessageDialog(addFrame, "The provided product line doesn't exist. Please enter a valid product line.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(addFrame, "An error occurred while adding the product. Please check your input and try again. Try changing the Product Code", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(addFrame, "An error occurred while adding the product. Please try again. Try changing the Product Code", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        });

    	    panel.add(new JLabel("Product Code:"));
    	    panel.add(productCodeInput);
    	    panel.add(new JLabel("Product Name:"));
    	    panel.add(productNameInput);
    	    panel.add(new JLabel("Product Line:"));
    	    panel.add(productLineDropdown);
    	    panel.add(new JLabel("Product Scale:"));
    	    panel.add(productScaleInput);
    	    panel.add(new JLabel("Product Vendor:"));
    	    panel.add(productVendorInput);
    	    panel.add(new JLabel("Product Description:"));
    	    panel.add(productDescriptionInput);
    	    panel.add(new JLabel("Quantity In Stock:"));
    	    panel.add(quantityInStockInput);
    	    panel.add(new JLabel("Buy Price:"));
    	    panel.add(buyPriceInput);
    	    panel.add(new JLabel("MSRP:"));
    	    panel.add(MSRPInput);

    	    panel.add(Box.createVerticalStrut(10)); // Adds 5 pixels of vertical space before button
    	    panel.add(saveButton);

    	    // Center alignment for all components
    	    for (Component comp : panel.getComponents()) {
    	        ((JComponent) comp).setAlignmentX(Component.CENTER_ALIGNMENT);
    	    }

    	    addFrame.getContentPane().add(panel);

    	    addFrame.setVisible(true);
    	}
    
    //---------------------

    private void addProduct(String productCode, String productName, String productLine, String productScale, String productVendor, String productDescription, int quantityInStock, double buyPrice, double MSRP) throws SQLException {
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();

            String insertQuery = "INSERT INTO products(productCode, productName, productLine, productScale, productVendor, productDescription, quantityInStock, buyPrice, MSRP) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
            preparedStatement.setString(1, productCode);
            preparedStatement.setString(2, productName);
            preparedStatement.setString(3, productLine);
            preparedStatement.setString(4, productScale);
            preparedStatement.setString(5, productVendor);
            preparedStatement.setString(6, productDescription);
            preparedStatement.setInt(7, quantityInStock);
            preparedStatement.setDouble(8, buyPrice);
            preparedStatement.setDouble(9, MSRP);

            preparedStatement.executeUpdate();
        } finally {
            dbConnection.closeConnection(conn);
        }
    }
    
    
    // --------------- [ Connecting to DB Table ] ------------------
    
    private void populateTable(DefaultTableModel model) {
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            String query = "SELECT * FROM products";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                model.addRow(rowData);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            dbConnection.closeConnection(conn);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new productsWindow());
    }
}
