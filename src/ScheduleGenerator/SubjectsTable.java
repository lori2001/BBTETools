package ScheduleGenerator;

import Common.logging.LogPanel;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static ScheduleGenerator.SGMainPanel.SG_LOG_INSTANCE;
import static java.lang.Math.min;

public class SubjectsTable {

    private ArrayList<Course> rows;

    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable table = new JTable(tableModel);
    private final JScrollPane scrollPane = new JScrollPane(table);
    private boolean settingDataInProgress = false;

    public SubjectsTable(ArrayList<Course> courses, Rectangle bounds) {
        tableModel.setColumnIdentifiers(Course.HEADERS);
        tableModel.fireTableDataChanged();

        table.setFillsViewportHeight(true);

        scrollPane.setBounds(bounds);

        setData(courses);
    }

    public void setData(ArrayList<Course> courses) {
        settingDataInProgress = true;
        tableModel.setRowCount(0);
        this.rows = courses;
        this.rows.forEach(row -> tableModel.addRow(row.getContentList().toArray()));
        settingDataInProgress = false;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public ArrayList<Course> getCourses() {
        if(settingDataInProgress) return rows;

        if(rows.size() > 0) {
            rows.clear();
            tableModel.getDataVector().forEach(row -> {
                String[] stringArray = Arrays.copyOf(row.toArray(), row.size(), String[].class);
                rows.add(new Course(stringArray, "621"));
            });
        } else {
            LogPanel.logln("VIGYÁZAT: A táblázat órákká való generálása", SG_LOG_INSTANCE);
        }

        return rows;
    }

    public void addTableModelListener(TableModelListener listener) {
        table.getModel().addTableModelListener(listener);
    }

    public boolean settingDataIsInProgress() {
        return settingDataInProgress;
    }

   /* @Override
    public void tableChanged(TableModelEvent e) {


        int row = e.getFirstRow();
        int column = e.getColumn();
        TableModel model = (TableModel)e.getSource();
        String columnName = model.getColumnName(column);
        Object data = model.getValueAt(row, column);

        System.out.println("table: "  + tableData[row][column]);

        System.out.println(row + " " + data + " " + column);
    }*/
}
