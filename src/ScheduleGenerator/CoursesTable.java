package ScheduleGenerator;

import Common.logging.LogPanel;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static ScheduleGenerator.SGMainPanel.SG_LOG_INSTANCE;

public class CoursesTable extends JPanel {

    public static final String[] TABLE_HEADERS = new String[]{
            "Ziua",
            "Orele",
            "Frecventa",
            "Sala",
            "Formatia",
            "Tipul",
            "Disciplina",
          //   "Cadrul didactic",
            "Törlés"
    };
    int DELETE_COL = 8;

    private ArrayList<Course> rows;

    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable table = new JTable(tableModel);
    private final JScrollPane scrollPane = new JScrollPane(table);
    private boolean settingDataInProgress = false;

    private final JButton addRow = new JButton("Új Óra (az elsõ órát duplázza oszt meglehet cserélni az adatait s a rajzon megjelenik :> )");

    public CoursesTable(Rectangle bounds) {
        setLayout(null);

        tableModel.setColumnIdentifiers(TABLE_HEADERS);
        tableModel.fireTableDataChanged();

        table.setFillsViewportHeight(true);

        setBounds(bounds);
        scrollPane.setBounds(0, 0, getWidth(), getHeight() - 75);
        addRow.setBounds(0, getHeight() - 75, 650, 25);

        add(addRow);
        add(scrollPane);

        addRow.addActionListener(e -> {
            this.rows.add(new Course(this.rows.get(this.rows.size() - 1)));
            tableModel.addRow(this.rows.get(this.rows.size() - 1).getContentList().toArray());
        });

        table.getModel().addTableModelListener(e -> {
            if(e.getColumn() == DELETE_COL && e.getFirstRow() == e.getLastRow()) {
                this.rows.remove(e.getFirstRow());
                tableModel.removeRow(e.getFirstRow());
            }
        });
    }

    public void setData(ArrayList<Course> courses) {
        if(courses != null) {
            settingDataInProgress = true;
            tableModel.setRowCount(0);

            this.rows = courses;

            this.rows.forEach(row -> tableModel.addRow(row.getContentList().toArray()));
            settingDataInProgress = false;
        }
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
            LogPanel.logln("VIGYÁZAT: A táblázat órákká való generálása sikertelen", SG_LOG_INSTANCE);
        }

        return rows;
    }

    public void addTableModelListener(TableModelListener listener) {
        table.getModel().addTableModelListener(listener);
    }

    public boolean settingDataIsInProgress() {
        return settingDataInProgress;
    }
}
