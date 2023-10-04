package com.transportSys.utils;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.RowFilter;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

public class ProductGUI {

    private JFrame frame;
    private JTable table;
    private JTextField productCodeSearch, productNameSearch, descriptionSearch;
    private DefaultTableModel tableModel;

    public ProductGUI() {
        frame = new JFrame("Product Descriptions");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        tableModel = new DefaultTableModel(new Object[]{"Product Code", "Product Name", "Description"}, 0);
        table = new JTable(tableModel);


        JPanel searchPanel = new JPanel();
        
        productCodeSearch = new JTextField(10);
        productNameSearch = new JTextField(10);
        descriptionSearch = new JTextField(10);

        productCodeSearch.getDocument().addDocumentListener(new DocumentListenerAdapter(() -> filter()));
        productNameSearch.getDocument().addDocumentListener(new DocumentListenerAdapter(() -> filter()));
        descriptionSearch.getDocument().addDocumentListener(new DocumentListenerAdapter(() -> filter()));

        searchPanel.add(new JLabel("Product Code:"));
        searchPanel.add(productCodeSearch);
        
        searchPanel.add(new JLabel("Product Name:"));
        searchPanel.add(productNameSearch);
        
        searchPanel.add(new JLabel("Description Keywords:"));
        searchPanel.add(descriptionSearch);

        frame.setLayout(new BorderLayout());
        frame.add(searchPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void filter() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        RowFilter<DefaultTableModel, Object> rf = null;

        // Create a compound filter for all three columns
        List<RowFilter<Object,Object>> filters = new ArrayList<>(3);
        filters.add(RowFilter.regexFilter(productCodeSearch.getText(), 0));
        filters.add(RowFilter.regexFilter(productNameSearch.getText(), 1));
        filters.add(RowFilter.regexFilter(descriptionSearch.getText(), 2));

        rf = RowFilter.andFilter(filters);

        sorter.setRowFilter(rf);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProductGUI());
    }

    // This is a simple adapter to convert DocumentListener into a lambda-friendly interface
    @FunctionalInterface
    interface SimpleDocumentListener extends javax.swing.event.DocumentListener {
        void update();

        @Override
        default void insertUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }

        @Override
        default void removeUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }

        @Override
        default void changedUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }
    }

    // A lambda-friendly adapter for our filtering needs
    static class DocumentListenerAdapter implements SimpleDocumentListener {
        private Runnable action;

        DocumentListenerAdapter(Runnable action) {
            this.action = action;
        }

        @Override
        public void update() {
            action.run();
        }
    }
}

