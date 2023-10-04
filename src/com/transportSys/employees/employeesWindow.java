package com.transportSys.employees;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;


import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.transportSys.payments.paymentsWindow;

public class employeesWindow extends JFrame{

    private JFrame frame;
    private JTable table;
    private JTextArea textArea;
    private DefaultTableModel tableModel;

    private String dbUrl = "jdbc:mysql://127.0.0.1:3306/classicmodels";
    private String user = "student";
    private String code = "student";

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    employeesWindow window = new employeesWindow();
                    window.setVisible(true); // use window instead of this
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    
   /* public List<String> search() {
    	
    	 for (String item : dataList) {
             if (item.toLowerCase().contains(query.toLowerCase())) {
                 listModel.addElement(item);
             }
         }
		return null;
    	
    }
*/
    
    public void searchEmployees(String searchText) {
        try {
            Connection con = DriverManager.getConnection(dbUrl, user, code);
            String query = "SELECT * FROM employees WHERE firstName LIKE ? OR lastName LIKE ? OR employeeNumber LIKE ? OR extension LIKE ? OR email LIKE ? OR officeCode LIKE ? OR reportsTo LIKE ? OR JobTitle LIKE ?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, "%" + searchText + "%");
            pstmt.setString(2, "%" + searchText + "%");
            pstmt.setString(3, "%" + searchText + "%");
            pstmt.setString(4, "%" + searchText + "%");
            pstmt.setString(5, "%" + searchText + "%");
            pstmt.setString(6, "%" + searchText + "%");
            pstmt.setString(7, "%" + searchText + "%");
            pstmt.setString(8, "%" + searchText + "%");


            ResultSet rs = pstmt.executeQuery();

            // Clear existing data in the table
            tableModel.setRowCount(0);

            // Add rows to the table
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                int cols = tableModel.getColumnCount();
                for (int i = 1; i <= cols; i++) {
                    row.add(rs.getObject(i));
                }
                tableModel.addRow(row);
            }

            // Close the ResultSet, PreparedStatement, and Connection
            rs.close();
            pstmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getEmployeeData() {
        try {
            Connection con = DriverManager.getConnection(dbUrl, user, code);
            Statement st = con.createStatement();
            String query = "select * from employees";
            ResultSet rs = st.executeQuery(query);

            // Create a DefaultTableModel for the JTable
            tableModel = new DefaultTableModel();
            table.setModel(tableModel);

            // Get the column names from the ResultSetMetaData
            int cols = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= cols; i++) {
                tableModel.addColumn(rs.getMetaData().getColumnName(i));
            }

            // Add rows to the table
            while (rs.next()) {
                Object[] row = new Object[cols];
                for (int i = 1; i <= cols; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(row);
            }

            // Close the ResultSet, Statement, and Connection
            rs.close();
            st.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the application.
     */
    public employeesWindow() {
        try {
            initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the contents of the frame.m
     */
    private void initialize() {
    	setTitle("Employees Window");
    	this.setSize(600, 400);
        this.frame.setLayout(new BorderLayout());
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(null);

 
        
        
        table = new JTable();
   
        JButton btnNewButton_1 = new JButton("New button");
        btnNewButton_1.setBounds(20, 330, 117, 29);
        frame.getContentPane().add(btnNewButton_1);
        
        JButton btnNewButton_2 = new JButton("New button");
        btnNewButton_2.setBounds(158, 330, 117, 29);
        frame.getContentPane().add(btnNewButton_2);
        
        JButton btnNewButton_3 = new JButton("Delete");
        btnNewButton_3.setBounds(307, 330, 117, 29);
        frame.getContentPane().add(btnNewButton_3);
             JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 68, 550, 250);
        frame.getContentPane().add(scrollPane);
        
        
        JButton btnNewButton = new JButton("Search");
        btnNewButton.setBounds(20, 28, 117, 29);
        frame.getContentPane().add(btnNewButton);

        textArea = new JTextArea();
        textArea.setBounds(230, 28, 209, 29);
        frame.getContentPane().add(textArea);
        getEmployeeData();
        // Add ActionListener to the Search button
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Call the employeeFinder method when the button is clicked
               // employeeFinder();
            	 String searchText = textArea.getText();
                 searchEmployees(searchText);
            }
        });
    }

}
