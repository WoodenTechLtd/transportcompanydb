package com.transportSys.productlines;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import com.transportSys.connection.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductLinesWindow extends JFrame {
    private DefaultListModel<String> productLinesListModel;
    private JList<String> productList;
    private JTextArea resultArea;
    private JPopupMenu contextMenu;
    private JTextField searchField;
  
   

    public ProductLinesWindow() {
        setTitle("Product Lines GUI");
        setSize(697, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        productLinesListModel = new DefaultListModel<>();
        productList = new JList<>(productLinesListModel);
        productList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        contextMenu = new JPopupMenu();
        productList.setComponentPopupMenu(contextMenu);

        JScrollPane scrollPane = new JScrollPane(productList);
        scrollPane.setPreferredSize(new Dimension(200, 300));

        JPanel productListPanel = new JPanel(new BorderLayout());
        
        // Create JLabel for the "Product Lines" header
        JLabel productListHeader = new JLabel("Product Lines");
        productListHeader.setFont(new Font("Arial", Font.BOLD, 14));
        productListPanel.add(productListHeader, BorderLayout.NORTH);
        productListPanel.add(scrollPane, BorderLayout.CENTER);

        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        resultArea.setWrapStyleWord(true);
        resultArea.setLineWrap(true);
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane resultScrollPane = new JScrollPane(resultArea);
        resultScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel resultAreaPanel = new JPanel(new BorderLayout());

        // Create JLabel for the "Text Description" header
        JLabel resultAreaHeader = new JLabel("Text Description");
        resultAreaHeader.setFont(new Font("Arial", Font.BOLD, 14));
        resultAreaPanel.add(resultAreaHeader, BorderLayout.NORTH);
        resultAreaPanel.add(resultScrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        searchField = new JTextField(20);
       
        panel.add(searchField);
        
        JButton addButton = new JButton("Add");
        panel.add(addButton);
        JButton editButton = new JButton("Edit");
        panel.add(editButton);
        JButton deleteButton = new JButton("Delete");
        panel.add(deleteButton);

        JMenuItem editProductLineMenuItem = new JMenuItem("Edit Product Line");
        JMenuItem deleteMenuItem = new JMenuItem("Delete");
        contextMenu.add(editProductLineMenuItem);
        contextMenu.add(deleteMenuItem);

        panel.add(new JLabel(" "));

       

       
        add(productListPanel, BorderLayout.WEST);
        add(resultAreaPanel, BorderLayout.CENTER);
        add(panel, BorderLayout.NORTH);
        

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchProductLine();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchProductLine();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Not needed for plain text components
            }
        });

       
        addButton.addActionListener(e -> addProductLine());
        editProductLineMenuItem.addActionListener(e -> editProductLine());
        deleteMenuItem.addActionListener(e -> deleteProductLine());
        editButton.addActionListener(e -> editProductLine());
        deleteButton.addActionListener(e -> deleteProductLine());
        productList.addListSelectionListener(e -> {
            if (productList.getSelectedValue() != null && productList.getSelectedValue().equals("Product Lines")) {
                resultArea.setText("");
            } else {
                refreshResult();
            }
        });

       

        setVisible(true);
        refreshProductLinesList();
    }
    
    

    private void refreshResult() {
        String selectedProductLine = productList.getSelectedValue();
        if (selectedProductLine != null) {
            try (Connection connection = dbConnection.getConnection()) {
                String query = "SELECT textDescription FROM productlines WHERE productLine = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, selectedProductLine);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String description = resultSet.getString("textDescription");
                    resultArea.setText(description);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage());
            }
        } else {
            resultArea.setText("");
        }
    }

    private void searchProductLine() {
        String searchText = searchField.getText().trim(); // Trim to remove leading/trailing spaces
        if (!searchText.isEmpty()) {
            try (Connection connection = dbConnection.getConnection()) {
                String query = "SELECT productLine FROM productlines WHERE productLine LIKE ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, "%" + searchText + "%");
                ResultSet resultSet = preparedStatement.executeQuery();

                productLinesListModel.clear(); // Clear the existing list

                while (resultSet.next()) {
                    String productLine = resultSet.getString("productLine");
                    productLinesListModel.addElement(productLine); // Add matching product lines
                }

                if (productLinesListModel.isEmpty()) {
                    resultArea.setText("No matching product lines found.");
                } else {
                    resultArea.setText(""); // Clear text description when matches are found
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage());
            }
        } else {
            refreshProductLinesList(); // Refresh the list with all product lines
            resultArea.setText(""); // Clear text description when search field is empty
        }
    }


    private void addProductLine() {
        String productName = "";
        String description = "";

        while (true) {
            JPanel inputPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            JTextField nameField = new JTextField(productName, 20);
            JTextArea descriptionArea = new JTextArea(description, 5, 20);
            JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);

            gbc.gridx = 0;
            gbc.gridy = 0;
            inputPanel.add(new JLabel("Product Line:"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            inputPanel.add(nameField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            inputPanel.add(new JLabel("Description:"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            inputPanel.add(descriptionScrollPane, gbc);

            int option = JOptionPane.showConfirmDialog(this, inputPanel, "Add Product Line", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.CANCEL_OPTION) {
                return; // User canceled the operation
            }

            productName = nameField.getText();
            description = descriptionArea.getText();

            if (!productName.isEmpty()) {
                if (description.isEmpty()) {
                    int confirmResult = JOptionPane.showConfirmDialog(
                        this,
                        "You have not added a text description. Are you sure you want to add this product line?",
                        "Confirm Add",
                        JOptionPane.YES_NO_OPTION
                    );

                    if (confirmResult != JOptionPane.YES_OPTION) {
                        // User doesn't want to proceed without a description, so continue the loop
                        continue;
                    }
                }

                int confirmResult = JOptionPane.showConfirmDialog(
                    this,
                    "Confirm adding product line '" + productName + "'?",
                    "Confirm Add",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirmResult == JOptionPane.YES_OPTION) {
                    try (Connection connection = dbConnection.getConnection()) {
                        String query = "INSERT INTO productlines (productLine, textDescription) VALUES (?, ?)";
                        PreparedStatement preparedStatement = connection.prepareStatement(query);
                        preparedStatement.setString(1, productName);
                        preparedStatement.setString(2, description);

                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            resultArea.setText(description);
                            refreshProductLinesList();
                            JOptionPane.showMessageDialog(this, "Product Line added successfully.");
                            productList.setSelectedValue(productName, true);
                        } else {
                            resultArea.setText("Failed to add product line.");
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage());
                    }
                }
                break; // Exit the loop when a product is successfully added
            } else {
                JOptionPane.showMessageDialog(this, "Product Line cannot be empty.");
            }
        }
    }



    private void editProductLine() {
        String selectedProductLine = productList.getSelectedValue();
        if (selectedProductLine != null) {
            try (Connection connection = dbConnection.getConnection()) {
                String query = "SELECT textDescription FROM productlines WHERE productLine = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, selectedProductLine);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String currentDescription = resultSet.getString("textDescription");
                    JTextArea textArea = new JTextArea(currentDescription, 5, 20);
                    textArea.setWrapStyleWord(true);
                    textArea.setLineWrap(true);

                    // Calculate the preferred number of rows for the text area based on the text length
                    int textAreaRows = Math.max(5, currentDescription.length() / 30); // Adjust the divisor as needed

                    // Calculate the preferred number of columns for the text area based on the text length
                    int textAreaColumns = Math.max(20, currentDescription.length() / textAreaRows);

                    textArea.setRows(textAreaRows);
                    textArea.setColumns(textAreaColumns);
                    

                    JPanel inputPanel = new JPanel(new GridBagLayout());
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.insets = new Insets(5, 5, 5, 5);

                    JTextField nameField = new JTextField(selectedProductLine, 20);
                    JScrollPane descriptionScrollPane = new JScrollPane(textArea);

                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    inputPanel.add(new JLabel("Product Line Name:"), gbc);

                    gbc.gridx = 1;
                    gbc.gridy = 0;
                    inputPanel.add(nameField, gbc);

                    gbc.gridx = 0;
                    gbc.gridy = 1;
                    inputPanel.add(new JLabel("Description:"), gbc);

                    gbc.gridx = 1;
                    gbc.gridy = 1;
                    gbc.gridwidth = 2;
                    inputPanel.add(descriptionScrollPane, gbc);

                    int editOption = JOptionPane.showConfirmDialog(
                        this,
                        inputPanel,
                        "Edit Product Line",
                        JOptionPane.OK_CANCEL_OPTION
                    );

                    if (editOption == JOptionPane.OK_OPTION) {
                        String newProductName = nameField.getText();
                        String newDescription = textArea.getText();

                        if (!selectedProductLine.equals(newProductName)) {
                            // Product Line name has been changed
                            int confirmResult = JOptionPane.showConfirmDialog(
                                    this,
                                    "Confirm editing product line name from '" + selectedProductLine + "' to '" + newProductName + "'?",
                                    "Confirm Edit",
                                    JOptionPane.YES_NO_OPTION
                            );

                            if (confirmResult == JOptionPane.YES_OPTION) {
                                String updateProductLineQuery = "UPDATE productlines SET productLine = ? WHERE productLine = ?";
                                PreparedStatement updateProductLineStatement = connection.prepareStatement(updateProductLineQuery);
                                updateProductLineStatement.setString(1, newProductName);
                                updateProductLineStatement.setString(2, selectedProductLine);

                                int rowsAffected = updateProductLineStatement.executeUpdate();

                                if (rowsAffected > 0) {
                                    resultArea.setText("Product Line name for '" + selectedProductLine + "' updated successfully to '" + newProductName + "'.");
                                    refreshProductLinesList();

                                    // Re-select the edited product line
                                    productList.setSelectedValue(newProductName, true);

                                    JOptionPane.showMessageDialog(this, "Product Line name updated successfully.");
                                } else {
                                    resultArea.setText("Product Line name update failed.");
                                }
                            } else {
                                JOptionPane.showMessageDialog(this, "No changes were made to the product line name.");
                            }
                        }

                        if (!currentDescription.equals(newDescription)) {
                            // Text Description has been changed
                            int confirmResult = JOptionPane.showConfirmDialog(
                                    this,
                                    "Confirm editing text description for product line '" + selectedProductLine + "'?",
                                    "Confirm Edit",
                                    JOptionPane.YES_NO_OPTION
                            );

                            if (confirmResult == JOptionPane.YES_OPTION) {
                                String updateTextDescriptionQuery = "UPDATE productlines SET textDescription = ? WHERE productLine = ?";
                                PreparedStatement updateTextDescriptionStatement = connection.prepareStatement(updateTextDescriptionQuery);
                                updateTextDescriptionStatement.setString(1, newDescription);
                                updateTextDescriptionStatement.setString(2, selectedProductLine);

                                int rowsAffected = updateTextDescriptionStatement.executeUpdate();

                                if (rowsAffected > 0) {
                                    resultArea.setText("New Text Description:\n\n" + newDescription);

                                    // Re-select the edited product line
                                    productList.setSelectedValue(selectedProductLine, true);

                                    JOptionPane.showMessageDialog(this, "Text Description updated successfully.");
                                } else {
                                    resultArea.setText("Text Description update failed.");
                                }
                            } else {
                                JOptionPane.showMessageDialog(this, "No changes were made to the text description.");
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage());
            }
        }
    }
    private void deleteProductLine() {
        String selectedProductLine = productList.getSelectedValue();
        if (selectedProductLine != null) {
            int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the product line '" + selectedProductLine + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
            );

            if (response == JOptionPane.YES_OPTION) {
                try (Connection connection = dbConnection.getConnection()) {
                    String deleteQuery = "DELETE FROM productlines WHERE productLine = ?";
                    PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                    deleteStatement.setString(1, selectedProductLine);

                    int rowsAffected = deleteStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        resultArea.setText("");
                        refreshProductLinesList();
                        JOptionPane.showMessageDialog(this, "Product Line deleted successfully.");
                       
                    } else {
                        resultArea.setText("Product Line '" + selectedProductLine + "' not found or delete failed.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "An error occurred: Cannot delete a Product Line associated with a product(s)");
                }
            }
        }
    }

    private void refreshProductLinesList() {
        productLinesListModel.clear();

        try (Connection connection = dbConnection.getConnection()) {
            String query = "SELECT productLine FROM productlines";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String productLine = resultSet.getString("productLine");
                productLinesListModel.addElement(productLine);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage());
        }
    }
    
   

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProductLinesWindow gui = new ProductLinesWindow();
            gui.refreshProductLinesList();
        });
    }
}  