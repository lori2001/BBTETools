package ScheduleGenerator;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;

public class CoursesList implements TableModelListener {

    String[] columnNames = Course.HEADERS;

    Object[][] tableData;
    JTable table;
    JScrollPane scrollPane;
    Rectangle bounds;

    public CoursesList(ArrayList<Course> courses, Rectangle bounds) {
        this.bounds = bounds;
        setData(courses);
    }

    public void setData(ArrayList<Course> courses) {
        tableData = new Object[courses.size()][Course.HEADERS.length];

        for(int i=0; i < courses.size(); i++){
            tableData[i] = courses.get(i).getContentList().toArray();
        }

        if(table != null) {
            AbstractTableModel dm = (AbstractTableModel)table.getModel();
            dm.fireTableDataChanged();
        }

        table = new JTable(tableData, columnNames);
        table.getModel().addTableModelListener(this);
        table.setFillsViewportHeight(true);

        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(bounds);
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        /*int row = e.getFirstRow();
        int column = e.getColumn();
        TableModel model = (TableModel)e.getSource();
        String columnName = model.getColumnName(column);
        Object data = model.getValueAt(row, column);

        System.out.println(row + " " + data + " " + column);*/
    }
}
