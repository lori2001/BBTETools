package ScheduleGenerator;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class SubjectsList extends JPanel {

    String[] columnNames = {"First Name",
            "Last Name",
            "Sport",
            "# of Years",
            "Vegetarian"};

    Object[][] data = {
            {"Kathy", "Smith",
                    "Snowboarding", 5, Boolean.FALSE},
            {"John", "Doe",
                    "Rowing", 3, Boolean.TRUE},
            {"Sue", "Black",
                    "Knitting", 2, Boolean.FALSE},
            {"Jane", "White",
                    "Speed reading", 20, Boolean.TRUE},
            {"Joe", "Brown",
                    "Pool", 10, Boolean.FALSE}
    };

    JTable table = new JTable(data, columnNames);

    public SubjectsList(ArrayList<Course> markedCourses) {

        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        add(scrollPane);
    }

}
