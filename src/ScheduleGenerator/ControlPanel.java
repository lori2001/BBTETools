package ScheduleGenerator;

import Common.logging.LogPanel;
import ScheduleGenerator.models.Major;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static ScheduleGenerator.data.SGData.*;

public class ControlPanel  extends JPanel {
    private final JComboBox<String> languageSelector = new JComboBox<>(
            LANGUAGE_OPTIONS
    );
    private final JComboBox<String> majorSelector = new JComboBox<>(
            MAJOR_OPTIONS
    );
    private final JComboBox<String> yearSelector = new JComboBox<>(
            STUD_YEAR_OPTIONS
    );
    private final JComboBox<String> groupSelector = new JComboBox<>();
    private final DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

    public ControlPanel(Point pos, Point size, Major initMajor, String initGroup, String initStudYear) {
        setLayout(new GridLayout(2, 4, 10, 0));
        setBounds(pos.x, pos.y, size.x, size.y);

        JLabel languageL = new JLabel("Nyelv:");
        JLabel majorL = new JLabel("Szak:");
        JLabel yearL = new JLabel("Év:");
        JLabel groupL = new JLabel("Csoport:");

        add(languageL);
        add(majorL);
        add(yearL);
        add(groupL);

        generateGroupOptions();
        groupSelector.setModel(model);

        languageSelector.addActionListener(setGroupOptions());
        majorSelector.addActionListener(setGroupOptions());
        yearSelector.addActionListener(setGroupOptions());

        languageSelector.setSelectedItem(initMajor.getLang());
        majorSelector.setSelectedItem(initMajor.getName());
        groupSelector.setSelectedItem(initGroup);
        yearSelector.setSelectedItem(initStudYear);

        add(languageSelector);
        add(majorSelector);
        add(yearSelector);
        add(groupSelector);
    }

    public void addActionListener(ActionListener actionListener) {
        languageSelector.addActionListener(actionListener);
        majorSelector.addActionListener(actionListener);
        yearSelector.addActionListener(actionListener);
        groupSelector.addActionListener(actionListener);
    }

    public String getSelectedGroup() {
        return (String) groupSelector.getSelectedItem();
    }

    private ActionListener setGroupOptions() {
        return e -> generateGroupOptions();
    }

    void generateGroupOptions() {
        ArrayList<String> groups = Parser.genGroupsFor(
                (String) languageSelector.getSelectedItem(),
                (String) majorSelector.getSelectedItem(),
                (String) yearSelector.getSelectedItem()
        );

        if(groups.size() != 0) {
            // removing old data
            model.removeAllElements();
            for (String gr : groups) {
                model.addElement(gr);
            }
        }else {
            LogPanel.logln("HIBA: Az adott szakra nem találtak a csoportok");
        }
    }
}
