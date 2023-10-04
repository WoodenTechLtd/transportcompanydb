package com.transportSys.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//class that listens for right click in table, and gives a copy option.

public class copyWindow {

    public static void addCopyOptionToTable(JTable table) {
        // Create a PopupMenu with a Copy option
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copyMenuItem = new JMenuItem("Copy");
        popupMenu.add(copyMenuItem);

        copyMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                if (row != -1 && col != -1) {
                    Object value = table.getValueAt(row, col);
                    StringSelection stringSelection = new StringSelection(value.toString());
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(stringSelection, null);
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // Check for right-click
                if (SwingUtilities.isRightMouseButton(e)) {
                    int r = table.rowAtPoint(e.getPoint());
                    int c = table.columnAtPoint(e.getPoint());

                    if (r != -1 && c != -1) {
                        table.setRowSelectionInterval(r, r);
                        table.setColumnSelectionInterval(c, c);
                    }

                    popupMenu.show(table, e.getX(), e.getY());
                }
            }
        });
    }
}
