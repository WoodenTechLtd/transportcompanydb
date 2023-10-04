package com.transportSys.payments;

import com.transportSys.utils.copyWindow;
import com.transportSys.connection.dbConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class paymentsWindow{
    private JFrame frame;
    private JTable table;
    private JScrollPane scrollPane;
    private JTextField customerNumberField;
    private JTextField checkNumberField;
    private JTextField paymentDateField;
    private JTextField amountField;
    private Connection connection;

    public paymentsWindow() {
    	
    	
        frame = new JFrame("Payments Window");
        this.frame.setLayout(new BorderLayout());
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // add window size, and open in center of screen
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null); // Center the window on the screen
        frame.setVisible(true);

        /// Setup search panel
      //JPanel searchPanel = new JPanel(new GridLayout(5, 2));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 60, 0));

        customerNumberField = new JTextField(5);
        checkNumberField = new JTextField(5);
        paymentDateField = new JTextField(5);
        amountField = new JTextField(5);

        // Adding KeyListeners to automatically search as the user types
        KeyAdapter searchOnKeyRelease = new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchPayments();
            }
        };

        customerNumberField.addKeyListener(searchOnKeyRelease);
        checkNumberField.addKeyListener(searchOnKeyRelease);
        paymentDateField.addKeyListener(searchOnKeyRelease);
        amountField.addKeyListener(searchOnKeyRelease);

        JButton searchButton = new JButton("Search");
        frame.getRootPane().setDefaultButton(searchButton);

        searchButton.addActionListener(e -> searchPayments());

        searchPanel.add(new JLabel("Customer Number:"));
        searchPanel.add(customerNumberField);
        searchPanel.add(new JLabel("Check Number:"));
        searchPanel.add(checkNumberField);
        searchPanel.add(new JLabel("Payment Date:"));
        searchPanel.add(paymentDateField);
        searchPanel.add(new JLabel("Amount:"));
        searchPanel.add(amountField);
        searchPanel.add(new JLabel("")); // filler for grid layout
        searchPanel.add(searchButton);

        frame.add(searchPanel, BorderLayout.NORTH);


        
        // Create table with empty model
        table = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // This will prevent the cells from being edited
            }
        };
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(true);
        table.setAutoCreateRowSorter(true);
        scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);
        copyWindow.addCopyOptionToTable(table);
        // Initialize the connection
        try {
            connection = dbConnection.getConnection();
            displayAllPayments();  // Initially display all payments
        } catch (SQLException e) {
            e.printStackTrace();
        }

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dbConnection.closeConnection(connection);
            }
        });

        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    private void displayAllPayments() {
        String sql = "SELECT * FROM payments";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            displayResultsInTable(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchPayments() {
        String sql = "SELECT * FROM payments WHERE customerNumber LIKE ? AND checkNumber LIKE ? AND paymentDate LIKE ? AND amount LIKE ?";
        
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + customerNumberField.getText() + "%");
            statement.setString(2, "%" + checkNumberField.getText() + "%");
            statement.setString(3, "%" + paymentDateField.getText() + "%");
            statement.setString(4, "%" + amountField.getText() + "%");
            ResultSet resultSet = statement.executeQuery();
            displayResultsInTable(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayResultsInTable(ResultSet resultSet) throws SQLException {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Customer Number", "Check Number", "Payment Date", "Amount"}, 0);
        while (resultSet.next()) {
            model.addRow(new Object[]{
                    resultSet.getInt("customerNumber"),
                    resultSet.getString("checkNumber"),
                    resultSet.getDate("paymentDate"),
                    resultSet.getBigDecimal("amount")
            });
        }
        table.setModel(model);
    }
    
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(paymentsWindow::new);
    }

}
