package ScheduleGenerator;

import Common.logging.LogPanel;
import ScheduleGenerator.data.SGData;
import ScheduleGenerator.records.Major;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static ScheduleGenerator.SGMainPanel.SG_LOG_INSTANCE;
import static ScheduleGenerator.data.SGData.*;

public class ControlPanel  extends JPanel {
    private final JComboBox<String> languageSelector = new JComboBox<>(
            LANGUAGE_OPTIONS
    );
    private final JComboBox<String> yearSelector = new JComboBox<>(
            STUD_YEAR_OPTIONS
    );

    private final JComboBox<String> majorSelector = new JComboBox<>();
    private final DefaultComboBoxModel<String> majorsModel = new DefaultComboBoxModel<>();
    private final JComboBox<String> groupSelector = new JComboBox<>();
    private final DefaultComboBoxModel<String> groupModel = new DefaultComboBoxModel<>();
    private final JComboBox<String> subGroupSelector = new JComboBox<>();
    private final DefaultComboBoxModel<String> subGroupModel = new DefaultComboBoxModel<>();

    public ControlPanel(Major initMajor, String initGroup, String initStudYear, String initSubGroup) {
        setLayout(new GridLayout(2, 5, 10, 0));

        JLabel languageL = new JLabel("Nyelv:");
        JLabel majorL = new JLabel("Szak:");
        JLabel yearL = new JLabel("Év:");
        JLabel groupL = new JLabel("Csoport:");
        JLabel subGroupL = new JLabel("Alcsoport:");

        add(languageL);
        add(majorL);
        add(yearL);
        add(groupL);
        add(subGroupL);

        languageSelector.setSelectedItem(initMajor.getLang());
        yearSelector.setSelectedItem(initStudYear);

        majorSelector.setModel(majorsModel);
        genMajorOptions();
        languageSelector.addActionListener(setMajorOptions());
        majorSelector.setSelectedItem(initMajor.getName());

        groupSelector.setModel(groupModel);
        genGroupOptions();
        languageSelector.addActionListener(setGroupOptions());
        majorSelector.addActionListener(setGroupOptions());
        yearSelector.addActionListener(setGroupOptions());
        groupSelector.setSelectedItem(initGroup);

        subGroupSelector.setModel(subGroupModel);
        genSubGroupOptions();
        groupSelector.addActionListener(setSubGroupOptions());
        subGroupSelector.setSelectedItem(initSubGroup);

        add(languageSelector);
        add(majorSelector);
        add(yearSelector);
        add(groupSelector);
        add(subGroupSelector);
    }

    public void addActionListener(ActionListener actionListener) {
        subGroupSelector.addActionListener(actionListener); // this field depends on every other field
    }

    public String getSelectedGroup() {
        return (String) groupSelector.getSelectedItem();
    }

    public String getSelectedSubGroup() {
        return (String) subGroupSelector.getSelectedItem();
    }

    private ActionListener setGroupOptions() {
        return e -> genGroupOptions();
    }
    private void genGroupOptions()
    {
        ArrayList<String> groups = GlobalParser.genGroupsFor(
                (String) languageSelector.getSelectedItem(),
                (String) majorSelector.getSelectedItem(),
                (String) yearSelector.getSelectedItem()
        );

        if(groups.size() != 0) {
            // removing old data
            groupModel.removeAllElements();
            for (String gr : groups) {
                groupModel.addElement(gr);
            }
        }else {
            LogPanel.logln("HIBA: Az adott szakra nem találtak a csoportok", SG_LOG_INSTANCE);
        }
    }

    private ActionListener setMajorOptions() {
        return e -> genMajorOptions();
    }

    private void genMajorOptions() {
        String selectedLang = (String) languageSelector.getSelectedItem();
        String[] majors = SGData.getMajorOptionsFor(selectedLang);

        if(majors.length != 0) {
            // removing old data
            majorsModel.removeAllElements();
            for (String major : majors) {
                majorsModel.addElement(major);
            }
        }else {
            LogPanel.logln("HIBA: Az adott szakra nem találtak a csoportok", SG_LOG_INSTANCE);
        }
    }

    private ActionListener setSubGroupOptions() {
        return e -> genSubGroupOptions();
    }

    private void genSubGroupOptions() {
        ArrayList<String> subgroups = GlobalParser.genSubGroupsFor(
                (String) languageSelector.getSelectedItem(),
                (String) majorSelector.getSelectedItem(),
                (String) yearSelector.getSelectedItem(),
                (String) groupSelector.getSelectedItem());

        if(subgroups.size() != 0) {
            // removing old data
            subGroupModel.removeAllElements();
            for (String major : subgroups) {
                subGroupModel.addElement(major);
            }
        }else {
            LogPanel.logln("HIBA: Az adott csoportnak nem találtak az alcsoportok", SG_LOG_INSTANCE);
        }
    }

}
