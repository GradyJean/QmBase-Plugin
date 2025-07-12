package com.qm.plugin.mybatismate.ui.component;

import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class DataTable extends JScrollPane {
    private final JBTable table;
    private Consumer<Integer> deleteCallback;

    public DataTable(String[] headers, Object[][] data) {
        super();
        // 初始化表格
        table = new JBTable();
        table.setRowHeight(24);
        // 填充数据
        fillData(headers, data);
        TableColumnModel columnModel = table.getColumnModel();
        TableColumn iconColumn = columnModel.getColumn(0);
        iconColumn.setPreferredWidth(15);
        iconColumn.setCellRenderer(new IconCellRenderer());
        columnModel.getColumn(1).setPreferredWidth(150);
        columnModel.getColumn(2).setPreferredWidth(150);
        columnModel.getColumn(3).setPreferredWidth(20);
        columnModel.getColumn(4).setPreferredWidth(20);
        // Add table to scroll pane and set layout
        setViewportView(table);
        addDeleteMenu();
    }

    private void addDeleteMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("删除");
        // 设置表格的选择模式
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) showPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != -1) {
                    table.setRowSelectionInterval(row, row); // 选中行
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        // 删除动作
        deleteItem.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                ((DefaultTableModel) table.getModel()).removeRow(selectedRow);
                if (deleteCallback != null) {
                    deleteCallback.accept(selectedRow);
                }
            }
        });
        // 添加右键菜单项到弹出菜单中
        popupMenu.add(deleteItem);
        // 设置弹出菜单为表格的右键菜单
        table.setComponentPopupMenu(popupMenu);
    }

    public void setDeleteCallback(Consumer<Integer> deleteCallback) {
        this.deleteCallback = deleteCallback;
    }

    public void updateColumn(Object value, int row, int column) {
        if (row >= 0 && row < table.getRowCount() && column >= 0 && column < table.getColumnCount()) {
            table.setValueAt(value, row, column);
        } else {
            throw new IndexOutOfBoundsException("Row or column index out of bounds");
        }
    }

    private void fillData(String[] headers, Object[][] data) {
        DefaultTableModel tableModel = new DefaultTableModel(data, headers) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(tableModel);
    }

    private static class IconCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, "", isSelected, hasFocus, row, column);

            if (value instanceof Icon) {
                label.setIcon((Icon) value);
                label.setText(""); // 不显示文字，只显示图标
            } else {
                label.setIcon(null);
                label.setText(value != null ? value.toString() : "");
            }

            label.setHorizontalAlignment(SwingConstants.CENTER); // 可选：居中对齐
            return label;
        }
    }

}
